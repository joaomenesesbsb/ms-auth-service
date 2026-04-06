package com.meneses.auth.features.auth.controller;

import com.meneses.auth.features.auth.service.AuthService;
import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticacao", description = "Endpoints de login e registro")
@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController{

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO loginResponseDTO = authService.login(request);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @Override
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        UserResponseDTO dto = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
