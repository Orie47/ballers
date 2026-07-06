package com.ballers.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Sent when a user wants to join a game. joinedAt isn't included - it's set automatically
// by the server (via GameParticipant's @CreationTimestamp), not supplied by the client.
@Getter
@Setter
public class GameParticipantRequest {

    @NotNull
    private Long userId;
}
