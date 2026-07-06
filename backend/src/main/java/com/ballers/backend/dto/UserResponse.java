package com.ballers.backend.dto;

import com.ballers.backend.model.SkillLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// What we send back to a client describing a user. Looks similar to the User entity today,
// but is a separate class on purpose: the entity is free to change shape (new internal
// fields, different relationships) without silently changing - or breaking - the API contract.
@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private String gender;
    private Integer heightCm;
    private SkillLevel skillLevel;
    // One entry per sport this user has been rated in - not a single blended number, since
    // skill (and rating) in one sport doesn't say anything about another.
    private List<SportRatingResponse> sportRatings;
    private Double lat;
    private Double lng;
}
