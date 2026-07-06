package com.ballers.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GameParticipantResponse {

    private Long id;
    private UserResponse user;
    private LocalDateTime joinedAt;
}
