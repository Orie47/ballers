package com.ballers.backend.controller;

import com.ballers.backend.dto.UserRequest;
import com.ballers.backend.dto.UserResponse;
import com.ballers.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// @RestController = @Controller + @ResponseBody: every method's return value is serialized
// straight to the HTTP response body as JSON, instead of being resolved to a view template.
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @Valid tells Spring to run Bean Validation against UserRequest's annotations
    // (@NotBlank, @NotNull, etc.) before this method body ever runs. If validation fails,
    // Spring throws MethodArgumentNotValidException - this method is never even entered -
    // and our GlobalExceptionHandler turns that into a 400 response.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
