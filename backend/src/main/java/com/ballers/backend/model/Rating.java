package com.ballers.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

// One user rating another user's play in a specific game.
@Entity
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Two separate @ManyToOne relationships to the same User entity. Each needs an explicit
    // @JoinColumn name (rater_id vs rated_player_id) - Hibernate can't safely guess distinct
    // column names on its own when two fields point at the same target entity type.
    @ManyToOne
    @JoinColumn(name = "rater_id")
    private User rater;

    @ManyToOne
    @JoinColumn(name = "rated_player_id")
    private User ratedPlayer;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    // Copied from game.getSport() at creation time. Ratings are only ever created for a
    // specific completed game, and a game's sport never changes, so this can't drift out of
    // sync with the source of truth - it just saves every "ratings for this player in this
    // sport" query from having to join through Game to get there.
    @Enumerated(EnumType.STRING)
    private Sport sport;

    private Integer score;
}
