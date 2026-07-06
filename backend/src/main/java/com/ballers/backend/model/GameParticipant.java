package com.ballers.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// The join table between Game and User, modeled as a real entity (rather than a plain
// @ManyToMany + @JoinTable) because we need an extra column here: joinedAt.
@Entity
// @Table lets us name the table explicitly and add a database-level constraint:
// the same user can never join the same game twice, enforced by Postgres itself.
@Table(name = "game_participants", uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "user_id"}))
@Getter
@Setter
public class GameParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The owning side of Game.participants - this foreign key is what actually links
    // a row here back to a specific Game.
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Hibernate-specific (not core JPA) annotation: automatically sets this field to the
    // current timestamp the moment the row is first inserted, and never touches it again
    // on later updates. Saves us from manually writing joinedAt = LocalDateTime.now().
    @CreationTimestamp
    private LocalDateTime joinedAt;
}
