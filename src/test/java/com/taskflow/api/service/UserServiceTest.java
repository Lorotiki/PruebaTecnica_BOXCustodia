package com.taskflow.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.taskflow.api.dto.UserResponse;
import com.taskflow.api.entity.User;
import com.taskflow.api.exception.NotFoundException;
import com.taskflow.api.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void listUsers_mapsResponse() {
        User user = new User();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@local");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.listUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Ana");
    }

    @Test
    void getUserOrThrow_throwsWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserOrThrow(99L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("User not found");
    }
}
