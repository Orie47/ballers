package com.ballers.backend.config;

import com.ballers.backend.dto.GameRequest;
import com.ballers.backend.dto.UserRequest;
import com.ballers.backend.dto.UserResponse;
import com.ballers.backend.model.SkillLevel;
import com.ballers.backend.model.Sport;
import com.ballers.backend.repository.GameRepository;
import com.ballers.backend.repository.UserRepository;
import com.ballers.backend.service.GameService;
import com.ballers.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// CommandLineRunner's run() method is called once, automatically, right after the Spring
// context finishes starting up. Used here to populate the database with sample data, so the
// app has realistic-looking users and games visible immediately instead of starting empty.
//
// This goes through UserService/GameService (the same services the controllers call) rather
// than building User/Game entities directly - that way seed data is created via the exact
// same validated business logic as a real API request would use, instead of a second,
// duplicate way of constructing entities that could drift out of sync with the real one.
@Component
public class DataSeeder implements CommandLineRunner {

    private static final double TEL_AVIV_LAT = 32.08;
    private static final double TEL_AVIV_LNG = 34.78;
    // How far, in degrees, users/games are scattered from the Tel Aviv center point.
    // ~0.05 degrees of latitude is roughly 5.5km, giving a realistic city-wide spread.
    private static final double SPREAD_DEGREES = 0.05;
    private static final int GAME_COUNT = 20;

    private static final List<String> FIRST_NAMES = List.of(
            "Noa", "Omer", "Yuval", "Tamar", "Itai", "Maya", "Roni", "Eden", "Amit", "Shira",
            "Daniel", "Liat", "Guy", "Michal", "Tom", "Ben", "Adi", "Nadav", "Gal", "Or"
    );

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final UserService userService;
    private final GameService gameService;
    private final Random random = new Random();

    public DataSeeder(UserRepository userRepository, GameRepository gameRepository,
                       UserService userService, GameService gameService) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void run(String... args) {
        // Only seed a genuinely empty database - otherwise every app restart during
        // development would pile on another 20 users and games on top of the last batch.
        if (userRepository.count() > 0 || gameRepository.count() > 0) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        for (String name : FIRST_NAMES) {
            UserResponse user = userService.createUser(randomUserRequest(name));
            userIds.add(user.getId());
        }

        Sport[] sports = Sport.values();
        for (int i = 0; i < GAME_COUNT; i++) {
            Long hostId = userIds.get(random.nextInt(userIds.size()));
            // Cycling through sports (rather than picking randomly) guarantees an even spread
            // across all four, instead of leaving one sport with zero sample games by chance.
            gameService.createGame(randomGameRequest(hostId, sports[i % sports.length]));
        }
    }

    private UserRequest randomUserRequest(String username) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setGender(random.nextBoolean() ? "M" : "F");
        request.setHeightCm(150 + random.nextInt(50));
        request.setSkillLevel(SkillLevel.values()[random.nextInt(SkillLevel.values().length)]);
        request.setLat(randomOffset(TEL_AVIV_LAT));
        request.setLng(randomOffset(TEL_AVIV_LNG));
        return request;
    }

    private GameRequest randomGameRequest(Long hostId, Sport sport) {
        GameRequest request = new GameRequest();
        request.setSport(sport);
        request.setLat(randomOffset(TEL_AVIV_LAT));
        request.setLng(randomOffset(TEL_AVIV_LNG));
        // Spread starts over the next two weeks, at varying hours, instead of every game
        // starting at the exact same moment.
        request.setStartTime(LocalDateTime.now()
                .plusDays(1 + random.nextInt(14))
                .plusHours(random.nextInt(12)));
        request.setPlayersNeeded(2 + random.nextInt(9));
        request.setHostId(hostId);
        return request;
    }

    private double randomOffset(double center) {
        return center + (random.nextDouble() * 2 - 1) * SPREAD_DEGREES;
    }
}
