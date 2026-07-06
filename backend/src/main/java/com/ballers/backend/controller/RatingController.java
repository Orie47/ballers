package com.ballers.backend.controller;

import com.ballers.backend.dto.RatingRequest;
import com.ballers.backend.dto.RatingResponse;
import com.ballers.backend.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse ratePlayer(@Valid @RequestBody RatingRequest request) {
        return ratingService.ratePlayer(request);
    }
}
