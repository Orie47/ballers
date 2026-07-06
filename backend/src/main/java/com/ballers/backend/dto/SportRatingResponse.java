package com.ballers.backend.dto;

import com.ballers.backend.model.Sport;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// One entry in a user's per-sport rating breakdown - see PlayerSportRating for why a single
// overall rating on User doesn't make sense.
@Getter
@Setter
public class SportRatingResponse {

    private Sport sport;
    private BigDecimal rating;
}
