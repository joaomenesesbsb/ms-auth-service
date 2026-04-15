package com.meneses.auth.features.auth.service;

import com.meneses.auth.features.auth.dto.*;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.security.JwtService;
import com.meneses.auth.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private RoleRepository roleRepository;

    public LoginResponseDTO login(@NonNull LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login attempt unregistered email address: [{}]", request.getEmail());
                    return new ResourceNotFoundException("User not found");
                });

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Invalid password provided by the user: [{}]", request.getEmail());
            throw new RuntimeException("Invalid password");
        }

        logger.info("User successfully authenticated: [{}]", request.getEmail());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(token, refreshToken);
    }

    public UserResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Attempt to register with an already registered email address: [{}]", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    logger.error("Critical failure: ROLE_USER  is not configured in the database!");
                    return new ResourceNotFoundException("Role not found");
                });

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("New user register with success: [{}]", user.getEmail());
        return new UserResponseDTO(user.getEmail());
    }

    public void logout(HttpServletRequest request) {
        if (request != null) {
            String token = jwtService.extractToken(request);
            long expirationTime = jwtService.getExpirationInSeconds(token);
            blacklistService.blacklistToken(token, expirationTime);
            logger.info("Token successfully blacklisted.");
        }
    }

    public LoginResponseDTO refreshToken(@NonNull RefreshTokenRequestDTO request){
        String oldToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(oldToken)) {
            logger.warn("Security Alert: Attempt to use non-refresh token for refresh operation");
            throw new SecurityException("Invalid token type");
        }

        if (blacklistService.isBlacklisted(oldToken)) {
            logger.error("Security Alert: Attempt to use a blacklisted token");
            throw new RuntimeException("Token is blacklisted");
        }

        String email = jwtService.extractUsername(oldToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Refresh failed: User not found for email [{}]", email);
                    return new ResourceNotFoundException("User not found");
                   });

        long expirationTime = jwtService.getExpirationInSeconds(oldToken);
        blacklistService.blacklistToken(oldToken, expirationTime);

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        logger.info("New refresh token successfully");
        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }
}
