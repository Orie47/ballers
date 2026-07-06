package com.ballers.backend.repository;

import com.ballers.backend.model.Game;
import com.ballers.backend.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    // Derived query method: Spring parses the method name itself. "findBy" means
    // "build a WHERE clause", and "Sport" is matched against the Game entity's `sport`
    // field, producing roughly: SELECT * FROM game WHERE sport = ?
    List<Game> findBySport(Sport sport);

    // Hand-written JPQL for a query the naming-convention approach can't express cleanly
    // (a range check plus an optional filter). This is the "rough" bounding-box filter used
    // by GameService.findNearbyGames - a cheap, index-friendly rectangle check that runs in
    // the database, before exact Haversine distance math happens in Java.
    // (:sport IS NULL OR g.sport = :sport) makes the sport filter optional: pass null and
    // this clause is always true, so nothing gets filtered out by sport.
    @Query("SELECT g FROM Game g WHERE "
            + "g.lat BETWEEN :minLat AND :maxLat AND "
            + "g.lng BETWEEN :minLng AND :maxLng AND "
            + "(:sport IS NULL OR g.sport = :sport)")
    List<Game> findByBoundingBox(@Param("minLat") double minLat,
                                  @Param("maxLat") double maxLat,
                                  @Param("minLng") double minLng,
                                  @Param("maxLng") double maxLng,
                                  @Param("sport") Sport sport);
}
