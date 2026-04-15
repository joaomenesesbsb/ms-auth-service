package com.meneses.auth.features.auth.controller;

import com.meneses.auth.features.auth.dto.*;
import com.meneses.auth.features.auth.service.AuthService;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController{

    private static final Logger logger = LogManager.getLogger(AuthControllerImpl.class);

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        logger.info("Login attempt for the user: [{}]", request.getEmail());

        LoginResponseDTO loginResponseDTO = authService.login(request);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @Override
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        logger.info("Registration request via email: [{}]", request.getEmail());

        UserResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        logger.info("Logout requested");

        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LoginResponseDTO> refreshToken(RefreshTokenRequestDTO request) {
        logger.info("Token refresh requested");

        LoginResponseDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
