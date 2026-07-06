package com.ballers.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// @RestControllerAdvice applies to every @RestController in the app - it's a single,
// centralized place to catch exceptions thrown anywhere in a request's handling (controller,
// service, repository) and turn them into a consistent JSON error response, instead of every
// controller method needing its own try/catch.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Thrown automatically by Spring when a @Valid-annotated @RequestBody fails its
    // Bean Validation constraints (@NotBlank, @NotNull, @Min, etc.) - the controller method
    // itself never runs. Turns every individual field failure into a readable line and
    // returns them all together as one 400 response.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Every "not found" (404) and "already full" / "lost the race" (409) error thrown across
    // our services uses this same exception type with a specific HttpStatus attached - this
    // handler just formats whichever status/message was thrown into our standard JSON shape.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        ErrorResponse body = new ErrorResponse(ex.getStatusCode().value(), ex.getReason(), null);
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
