package com.meneses.auth.features.auth.controller;

import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {

    @Operation(summary = "Login de usuario",
            description = "Realiza login, valida senha, gera token JWT e gera refresh token"
    )
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Login realizado com sucesso",  content = @Content( mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse( responseCode = "401",  description = "Credenciais inválidas",  content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "400",  description = "Dados inválidos",  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "500",  description = "Erro interno",  content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request);

    @Operation(summary = "Registrar novo usuario", description = "Cria um novo usuário com role padrão USER")
    @ApiResponses({
            @ApiResponse( responseCode = "201", description = "Usuário cadastrado com sucesso", content = @Content( mediaType = "application/json",  schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse( responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "409", description = "Email já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request);
}
