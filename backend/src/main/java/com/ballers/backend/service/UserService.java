package com.ballers.backend.service;

import com.ballers.backend.dto.UserRequest;
import com.ballers.backend.dto.UserResponse;
import com.ballers.backend.mapper.UserMapper;
import com.ballers.backend.model.User;
import com.ballers.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setGender(request.getGender());
        user.setHeightCm(request.getHeightCm());
        user.setSkillLevel(request.getSkillLevel());
        user.setLat(request.getLat());
        user.setLng(request.getLng());

        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userMapper.toResponse(user);
    }
}
