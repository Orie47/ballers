package com.ballers.backend.repository;

import com.ballers.backend.model.PlayerSportRating;
import com.ballers.backend.model.Sport;
import com.ballers.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerSportRatingRepository extends JpaRepository<PlayerSportRating, Long> {

    // Used when recalculating: is there already a row for this user + sport to update,
    // or do we need to create the first one?
    Optional<PlayerSportRating> findByUserAndSport(User user, Sport sport);

    // Used by UserMapper to build the full list of per-sport ratings for a profile response.
    List<PlayerSportRating> findByUser(User user);
}
