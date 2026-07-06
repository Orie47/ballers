package com.ballers.backend.dto;

import lombok.Getter;
import lombok.Setter;

// gameId stays a plain id rather than a nested GameResponse on purpose - nesting the full
// game (with its own participants list) here isn't needed and would bloat the payload.
@Getter
@Setter
public class RatingResponse {

    private Long id;
    private UserResponse rater;
    private UserResponse ratedPlayer;
    private Long gameId;
    private Integer score;
}
