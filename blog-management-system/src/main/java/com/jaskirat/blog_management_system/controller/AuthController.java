package com.jaskirat.blog_management_system.controller;

import com.jaskirat.blog_management_system.dto.AuthResponse;
import com.jaskirat.blog_management_system.dto.LoginRequest;
import com.jaskirat.blog_management_system.dto.RegisterRequest;
import com.jaskirat.blog_management_system.dto.UserResponse;
import com.jaskirat.blog_management_system.security.JwtService;
import com.jaskirat.blog_management_system.security.UserPrincipal;
import com.jaskirat.blog_management_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var authentication = authenticationManager.authenticate(authToken);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return ResponseEntity.ok(new AuthResponse(token, principal.getUsername()));
    }
}