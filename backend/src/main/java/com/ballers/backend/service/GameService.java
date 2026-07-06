package com.ballers.backend.service;

import com.ballers.backend.dto.GameRequest;
import com.ballers.backend.dto.GameResponse;
import com.ballers.backend.mapper.UserMapper;
import com.ballers.backend.model.Game;
import com.ballers.backend.model.GameParticipant;
import com.ballers.backend.model.Sport;
import com.ballers.backend.model.User;
import com.ballers.backend.repository.GameParticipantRepository;
import com.ballers.backend.repository.GameRepository;
import com.ballers.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Marks this as a Spring-managed bean so it can be constructor-injected wherever it's needed
// (e.g. a future GameController).
@Service
public class GameService {

    private static final int PAGE_SIZE = 20;
    // Earth's radius in km, used to convert the Haversine formula's angular result into an
    // actual distance.
    private static final double EARTH_RADIUS_KM = 6371.0;
    // Roughly how many km one degree of latitude covers, everywhere on Earth.
    private static final double KM_PER_DEGREE_LAT = 111.0;

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final UserMapper userMapper;

    // Direct access to the persistence context, used in joinGame() to force an optimistic
    // lock check on Game even though joining doesn't change any of Game's own scalar fields
    // (see the comment inside joinGame for why that's necessary).
    @PersistenceContext
    private EntityManager entityManager;

    public GameService(GameRepository gameRepository, UserRepository userRepository,
                        GameParticipantRepository gameParticipantRepository, UserMapper userMapper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameParticipantRepository = gameParticipantRepository;
        this.userMapper = userMapper;
    }

    // Creates a game with the given host and no participants yet.
    public GameResponse createGame(GameRequest request) {
        User host = userRepository.findById(request.getHostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host not found"));

        Game game = new Game();
        game.setSport(request.getSport());
        game.setLat(request.getLat());
        game.setLng(request.getLng());
        game.setStartTime(request.getStartTime());
        game.setPlayersNeeded(request.getPlayersNeeded());
        game.setHost(host);

        Game saved = gameRepository.save(game);

        return toGameResponse(saved, null);
    }

    public Page<GameResponse> findNearbyGames(double lat, double lng, double radiusKm, Sport sport, int page) {
        // --- Step 1: rough bounding-box filter, done in the database ---
        // Convert the radius into a lat/lng rectangle around the user. Latitude degrees are a
        // constant ~111km everywhere; longitude degrees shrink the closer you are to the
        // poles, so we divide by cos(latitude) to correct the box width for that.
        double latDelta = radiusKm / KM_PER_DEGREE_LAT;
        double lngDelta = radiusKm / (KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(lat)));

        double minLat = lat - latDelta;
        double maxLat = lat + latDelta;
        double minLng = lng - lngDelta;
        double maxLng = lng + lngDelta;

        // This is an index-friendly range query - much cheaper than computing exact distance
        // math against every row in the table.
        List<Game> candidates = gameRepository.findByBoundingBox(minLat, maxLat, minLng, maxLng, sport);

        // --- Step 2: exact distance, computed in Java ---
        // The bounding box is a rectangle, but the real search area is a circle, so some
        // candidates in the box are still farther than radiusKm (the box's corners stick out
        // past the circle). We compute the real distance to each, throw out anything actually
        // outside the radius, then sort nearest-first.
        List<GameResponse> matches = candidates.stream()
                .map(game -> new AbstractMap.SimpleEntry<>(game, haversineDistanceKm(lat, lng, game.getLat(), game.getLng())))
                .filter(entry -> entry.getValue() <= radiusKm)
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .map(entry -> toGameResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // --- Step 3: paginate the already-sorted, already-filtered list ---
        // Pagination happens here, in memory, because we can only know the correct order
        // (nearest-first) after computing exact distances above - the database query in
        // step 1 has no idea about real distance, only the rough box.
        int fromIndex = Math.min(page * PAGE_SIZE, matches.size());
        int toIndex = Math.min(fromIndex + PAGE_SIZE, matches.size());
        List<GameResponse> pageContent = matches.subList(fromIndex, toIndex);

        // PageImpl carries both this page's content and the total match count, so a client
        // can render something like "page 2 of 5".
        return new PageImpl<>(pageContent, PageRequest.of(page, PAGE_SIZE), matches.size());
    }

    // Adds a user to a game, rejecting the join with 409 Conflict if the game is already full,
    // or if two people raced for the last spot and this request lost the race.
    @Transactional
    public GameResponse joinGame(Long gameId, Long userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // First-pass check using data we've already loaded. This alone is NOT enough to
        // prevent two concurrent requests from both passing this check when there's exactly
        // one spot left - that's what the version lock below is for.
        if (game.getParticipants().size() >= game.getPlayersNeeded()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is already full");
        }

        // Game.participants is owned by GameParticipant (mappedBy = "game"), so inserting a
        // new GameParticipant row does NOT, by itself, change any column on the game table -
        // meaning Hibernate would never bump Game.version or run a version check just from
        // this insert. Since the invariant we're protecting ("is the game full") depends on
        // counting rows in a related table rather than a field on Game itself, we have to
        // explicitly opt Game's row into the optimistic lock check. OPTIMISTIC_FORCE_INCREMENT
        // tells Hibernate: "treat this like Game was modified - check its version, and bump it,
        // when this transaction commits."
        entityManager.lock(game, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        GameParticipant participant = new GameParticipant();
        participant.setGame(game);
        participant.setUser(user);
        gameParticipantRepository.save(participant);

        try {
            // Forces Hibernate to run the pending SQL right now (the INSERT for the
            // participant, and the version-checked UPDATE for the game) instead of waiting
            // until the transaction commits - so we can catch a failed version check here
            // and turn it into a clean 409, rather than an uncaught exception after the
            // method has already returned.
            entityManager.flush();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Someone else just took that spot - please try again");
        }

        game.getParticipants().add(participant);
        return toGameResponse(game, null);
    }

    // The Haversine formula: gives the real "great-circle" distance between two lat/lng
    // points on a sphere, in km. A plain straight-line distance formula would be wrong here,
    // since a degree of longitude covers a different physical distance depending on latitude.
    private double haversineDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    // Converts a Game entity (plus its computed distance) into the DTO we actually return
    // from the API - keeps the entity's internal shape decoupled from the API response shape.
    // distanceKm is nullable since it's only meaningful for findNearbyGames - callers like
    // joinGame() that aren't relative to any particular search location pass null.
    private GameResponse toGameResponse(Game game, Double distanceKm) {
        GameResponse response = new GameResponse();
        response.setId(game.getId());
        response.setSport(game.getSport());
        response.setLat(game.getLat());
        response.setLng(game.getLng());
        response.setStartTime(game.getStartTime());
        response.setPlayersNeeded(game.getPlayersNeeded());
        response.setHost(userMapper.toResponse(game.getHost()));
        response.setParticipants(game.getParticipants().stream()
                .map(GameParticipant::getUser)
                .map(userMapper::toResponse)
                .collect(Collectors.toList()));
        response.setVersion(game.getVersion());
        response.setDistanceKm(distanceKm);
        return response;
    }
}
