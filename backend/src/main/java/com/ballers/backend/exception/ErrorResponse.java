package com.ballers.backend.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// The one consistent JSON shape every error from this API returns, regardless of whether it
// came from bean validation, a missing resource, or a business-rule conflict.
@Getter
@Setter
public class ErrorResponse {

    private int status;
    private String message;
    // Only populated for validation errors, where there can be more than one problem at once
    // (e.g. both username and heightCm invalid in the same request).
    private List<String> errors;

    public ErrorResponse(int status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
