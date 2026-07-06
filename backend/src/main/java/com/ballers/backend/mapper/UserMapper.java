package com.ballers.backend.mapper;

import com.ballers.backend.dto.SportRatingResponse;
import com.ballers.backend.dto.UserResponse;
import com.ballers.backend.model.PlayerSportRating;
import com.ballers.backend.model.Sport;
import com.ballers.backend.model.User;
import com.ballers.backend.repository.PlayerSportRatingRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Shared by every service that needs to convert a User entity into the DTO exposed over the
// API, so this conversion lives in exactly one place instead of being copy-pasted into each
// service that touches a User (GameService, RatingService, ...).
//
// This is a Spring-managed @Component (not a static utility) because building a full
// UserResponse now requires a database lookup - the per-sport ratings aren't a field on
// User itself, they live in a separate table.
@Component
public class UserMapper {

    private final PlayerSportRatingRepository playerSportRatingRepository;

    public UserMapper(PlayerSportRatingRepository playerSportRatingRepository) {
        this.playerSportRatingRepository = playerSportRatingRepository;
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setGender(user.getGender());
        response.setHeightCm(user.getHeightCm());
        response.setSkillLevel(user.getSkillLevel());
        response.setLat(user.getLat());
        response.setLng(user.getLng());

        // Only sports this user has actually been rated in have a PlayerSportRating row.
        Map<Sport, BigDecimal> ratingBySport = playerSportRatingRepository.findByUser(user).stream()
                .collect(Collectors.toMap(PlayerSportRating::getSport, PlayerSportRating::getRating));

        // Build one entry per sport that exists, not just the ones the user happens to have a
        // row for - so a client can always render all four sports on a profile, with rating
        // null for any the user hasn't been rated in yet, rather than that sport silently
        // being missing from the list.
        List<SportRatingResponse> sportRatings = Arrays.stream(Sport.values())
                .map(sport -> toSportRatingResponse(sport, ratingBySport.get(sport)))
                .collect(Collectors.toList());

        response.setSportRatings(sportRatings);

        return response;
    }

    private SportRatingResponse toSportRatingResponse(Sport sport, BigDecimal rating) {
        SportRatingResponse response = new SportRatingResponse();
        response.setSport(sport);
        // Null (rather than e.g. zero) when this user has no PlayerSportRating row for this
        // sport yet - zero would wrongly imply "rated, and rated terribly."
        response.setRating(rating);
        return response;
    }
}
