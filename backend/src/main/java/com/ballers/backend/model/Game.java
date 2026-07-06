package com.ballers.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stored as the enum's name ("BASKETBALL", etc.), same reasoning as User.skillLevel.
    @Enumerated(EnumType.STRING)
    private Sport sport;

    private Double lat;

    private Double lng;

    private LocalDateTime startTime;

    private Integer playersNeeded;

    // Many games can point to the same host User. This is the "owning" side of the
    // relationship: it creates a "host_id" foreign key column on the game table.
    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    // The other side of the relationship owned by GameParticipant.game (see that class).
    // mappedBy tells Hibernate "don't create a second foreign key column for this - just
    // look at the `game` field over on GameParticipant to figure out which rows belong here."
    @OneToMany(mappedBy = "game")
    private Set<GameParticipant> participants = new HashSet<>();

    // Optimistic locking counter. Hibernate increments this on every update, and adds
    // "WHERE version = ?" to its UPDATE statements. If two requests read the same row and
    // both try to save, the second one's WHERE clause matches zero rows (since the first
    // update already bumped the version) and Hibernate throws OptimisticLockException instead
    // of silently applying a stale, conflicting update. Matters for races like two users
    // joining the last open spot in a game at the same time.
    @Version
    private Long version;
}
