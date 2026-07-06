package com.ballers.backend.dto;

import com.ballers.backend.model.Sport;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// What a client sends to create/update a game. Note hostId is a plain Long, not a nested
// User object - a client just knows "the host is user #7", it doesn't have a full User
// object to send, and doesn't need one for this purpose.
@Getter
@Setter
public class GameRequest {

    @NotNull
    private Sport sport;

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private Double lat;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private Double lng;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Min(1)
    private Integer playersNeeded;

    @NotNull
    private Long hostId;
}
