package com.meneses.auth.controllers;

import com.meneses.auth.dto.LoginRequest;
import com.meneses.auth.dto.LoginResponse;
import com.meneses.auth.dto.RegisterRequest;
import com.meneses.auth.dto.UserResponse;
import com.meneses.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse dto = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

}
