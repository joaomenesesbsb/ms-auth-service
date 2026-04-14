package com.meneses.auth.features.auth.service;

import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.security.JwtService;
import com.meneses.auth.security.TokenBlacklistService;
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
                    return new ResourceNotFoundException("Not founnd user");
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
                    return new ResourceNotFoundException("Role não encontrada");
                });

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("New user register with success: [{}]", user.getEmail());
        return new UserResponseDTO(user.getEmail());
    }

    public void logout(String token) {
        if (token != null) {
            long expirationTime = jwtService.getExpirationInSeconds(token);
            blacklistService.blacklistToken(token, expirationTime);
            logger.info("Token successfully blacklisted.");
        }
    }
}
