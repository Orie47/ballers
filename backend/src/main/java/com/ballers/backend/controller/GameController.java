package com.ballers.backend.controller;

import com.ballers.backend.dto.GameParticipantRequest;
import com.ballers.backend.dto.GameRequest;
import com.ballers.backend.dto.GameResponse;
import com.ballers.backend.model.Sport;
import com.ballers.backend.service.GameService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createGame(@Valid @RequestBody GameRequest request) {
        return gameService.createGame(request);
    }

    // sport and page are genuinely optional query parameters (page defaults to the first
    // page, sport defaults to "no filter"). lat/lng/radius are also declared as optional
    // query parameters (required = false) rather than path/required parameters, but a
    // "nearby games" search is meaningless without a location - so we check for them
    // ourselves and return a clean 400 if they're missing, instead of quietly searching
    // around (0, 0) or throwing an unhandled NullPointerException deeper in the service.
    @GetMapping
    public Page<GameResponse> findNearbyGames(
            @RequestParam(required = false) Sport sport,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius,
            @RequestParam(defaultValue = "0") int page) {

        if (lat == null || lng == null || radius == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "lat, lng, and radius are required to search for nearby games");
        }

        return gameService.findNearbyGames(lat, lng, radius, sport, page);
    }

    @PostMapping("/{id}/join")
    public GameResponse joinGame(@PathVariable Long id, @Valid @RequestBody GameParticipantRequest request) {
        return gameService.joinGame(id, request.getUserId());
    }
}
