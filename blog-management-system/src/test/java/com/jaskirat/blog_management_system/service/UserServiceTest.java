package com.jaskirat.blog_management_system.service;

import com.jaskirat.blog_management_system.dto.RegisterRequest;
import com.jaskirat.blog_management_system.dto.UserResponse;
import com.jaskirat.blog_management_system.entity.Role;
import com.jaskirat.blog_management_system.entity.User;
import com.jaskirat.blog_management_system.repository.RoleRepository;
import com.jaskirat.blog_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;
    private Role userRole;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setUsername("jaskirat");
        request.setEmail("j@test.com");
        request.setPassword("test123");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
    }

    @Test
    void register_successfulRegistration_returnsUserResponse() {
        // Arrange: tell the fakes how to behave
        when(userRepository.existsByUsername("jaskirat")).thenReturn(false);
        when(userRepository.existsByEmail("j@test.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("test123")).thenReturn("hashed_password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("jaskirat");
        savedUser.setEmail("j@test.com");
        savedUser.setPassword("hashed_password");
        savedUser.setRole(userRole);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.register(request);

        // Assert
        assertThat(response.getUsername()).isEqualTo("jaskirat");
        assertThat(response.getEmail()).isEqualTo("j@test.com");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");

        // Verify the password was actually hashed before saving, never stored raw
        verify(passwordEncoder).encode("test123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_usernameAlreadyTaken_throwsException() {
        when(userRepository.existsByUsername("jaskirat")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));

        // Confirm we never even got as far as trying to save
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_emailAlreadyTaken_throwsException() {
        when(userRepository.existsByUsername("jaskirat")).thenReturn(false);
        when(userRepository.existsByEmail("j@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }
}