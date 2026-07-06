package com.ballers.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Sent to submit a rating. All references are plain ids (raterId/ratedPlayerId/gameId) -
// same reasoning as GameRequest.hostId: the client identifies related things by id,
// it doesn't send whole nested objects.
@Getter
@Setter
public class RatingRequest {

    @NotNull
    private Long raterId;

    @NotNull
    private Long ratedPlayerId;

    @NotNull
    private Long gameId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;
}
