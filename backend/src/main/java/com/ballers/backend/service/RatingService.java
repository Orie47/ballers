package com.ballers.backend.service;

import com.ballers.backend.dto.RatingRequest;
import com.ballers.backend.dto.RatingResponse;
import com.ballers.backend.mapper.UserMapper;
import com.ballers.backend.model.Game;
import com.ballers.backend.model.PlayerSportRating;
import com.ballers.backend.model.Rating;
import com.ballers.backend.model.Sport;
import com.ballers.backend.model.User;
import com.ballers.backend.repository.GameRepository;
import com.ballers.backend.repository.PlayerSportRatingRepository;
import com.ballers.backend.repository.RatingRepository;
import com.ballers.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RatingService {

    private static final int AVERAGE_SCALE = 2;

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PlayerSportRatingRepository playerSportRatingRepository;
    private final UserMapper userMapper;

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository,
                          GameRepository gameRepository, PlayerSportRatingRepository playerSportRatingRepository,
                          UserMapper userMapper) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.playerSportRatingRepository = playerSportRatingRepository;
        this.userMapper = userMapper;
    }

    // Saves a new Rating, then recalculates the rated player's stored average for that game's
    // sport specifically - both in the same transaction, so the two writes either both succeed
    // or both roll back together.
    @Transactional
    public RatingResponse ratePlayer(RatingRequest request) {
        User rater = userRepository.findById(request.getRaterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rater not found"));

        User ratedPlayer = userRepository.findById(request.getRatedPlayerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rated player not found"));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        Rating rating = new Rating();
        rating.setRater(rater);
        rating.setRatedPlayer(ratedPlayer);
        rating.setGame(game);
        rating.setSport(game.getSport());
        rating.setScore(request.getScore());
        ratingRepository.save(rating);

        recalculateAverageRating(ratedPlayer, game.getSport());

        return toRatingResponse(rating);
    }

    // Recomputes ratedPlayer's average for one specific sport, from every Rating row that
    // player has received in that sport, and saves it onto that sport's PlayerSportRating row
    // (creating it if this is the player's first rating in that sport).
    // findByRatedPlayerAndSport below queries the same "rating" table the save() above just
    // wrote to - Hibernate auto-flushes the pending insert before running that query, so the
    // just-added rating is included without us having to flush manually.
    private void recalculateAverageRating(User player, Sport sport) {
        List<Rating> ratings = ratingRepository.findByRatedPlayerAndSport(player, sport);

        BigDecimal total = ratings.stream()
                .map(Rating::getScore)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = total.divide(BigDecimal.valueOf(ratings.size()), AVERAGE_SCALE, RoundingMode.HALF_UP);

        PlayerSportRating sportRating = playerSportRatingRepository.findByUserAndSport(player, sport)
                .orElseGet(() -> {
                    PlayerSportRating newRating = new PlayerSportRating();
                    newRating.setUser(player);
                    newRating.setSport(sport);
                    return newRating;
                });

        sportRating.setRating(average);
        playerSportRatingRepository.save(sportRating);
    }

    private RatingResponse toRatingResponse(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setRater(userMapper.toResponse(rating.getRater()));
        response.setRatedPlayer(userMapper.toResponse(rating.getRatedPlayer()));
        response.setGameId(rating.getGame().getId());
        response.setScore(rating.getScore());
        return response;
    }
}
