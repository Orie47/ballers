package com.ballers.backend.repository;

import com.ballers.backend.model.Rating;
import com.ballers.backend.model.Sport;
import com.ballers.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Derived query method: matched against Rating's `ratedPlayer` and `sport` fields,
    // producing WHERE rated_player_id = ? AND sport = ?. Used to pull every rating a given
    // player has received for one specific sport, so we can recompute their per-sport average.
    List<Rating> findByRatedPlayerAndSport(User ratedPlayer, Sport sport);
}
