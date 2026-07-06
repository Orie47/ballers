package com.ballers.backend.dto;

import com.ballers.backend.model.SkillLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// What a client is allowed to send when creating/updating a user profile.
// No `id` (server-assigned) and no `rating` (computed from other users' Ratings,
// not something a client should be able to set directly).
@Getter
@Setter
public class UserRequest {

    @NotBlank
    private String username;

    private String gender;

    @NotNull
    @Positive
    private Integer heightCm;

    @NotNull
    private SkillLevel skillLevel;

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private Double lat;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private Double lng;
}
