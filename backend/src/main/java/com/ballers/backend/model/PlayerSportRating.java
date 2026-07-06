package com.ballers.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// A user's average rating for one specific sport. Replaces a single overall User.rating,
// since skill in one sport says nothing about skill in another - an advanced basketball
// player might be a complete beginner at tennis, so one blended number isn't meaningful.
// One row per (user, sport) pair - enforced at the database level below.
@Entity
@Table(name = "player_sport_ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sport"}))
@Getter
@Setter
public class PlayerSportRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Sport sport;

    private BigDecimal rating = BigDecimal.ZERO;
}
