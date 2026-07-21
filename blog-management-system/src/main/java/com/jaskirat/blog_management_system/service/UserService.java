package com.jaskirat.blog_management_system.service;

import com.jaskirat.blog_management_system.dto.RegisterRequest;
import com.jaskirat.blog_management_system.dto.UserResponse;
import com.jaskirat.blog_management_system.entity.Role;
import com.jaskirat.blog_management_system.entity.User;
import com.jaskirat.blog_management_system.repository.RoleRepository;
import com.jaskirat.blog_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role not found - did you seed the database?"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash, never raw
        user.setRole(userRole);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName()
        );
    }
}