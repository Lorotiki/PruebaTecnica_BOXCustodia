package com.taskflow.api.service;

import com.taskflow.api.dto.UserResponse;
import com.taskflow.api.entity.User;
import com.taskflow.api.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new com.taskflow.api.exception.NotFoundException("User not found: " + userId));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
