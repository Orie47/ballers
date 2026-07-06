package com.ballers.backend.dto;

import com.ballers.backend.model.Sport;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// What we send back describing a game. Nests full UserResponse objects for host/participants
// so a client can show names/skill level etc. without a second API call.
@Getter
@Setter
public class GameResponse {

    private Long id;
    private Sport sport;
    private Double lat;
    private Double lng;
    private LocalDateTime startTime;
    private Integer playersNeeded;
    private UserResponse host;
    private List<UserResponse> participants;
    // Echoed back to the client so a future "update this game" request can include it,
    // letting the server detect if someone else modified the game in between
    // (see Game.version / optimistic locking).
    private Long version;
    // Only populated by GameService.findNearbyGames - null in other contexts (e.g. a plain
    // "get game by id" endpoint that isn't relative to any particular user location).
    private Double distanceKm;
}
