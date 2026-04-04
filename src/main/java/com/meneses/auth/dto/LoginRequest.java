package com.meneses.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Schema(description = "Email do usuário", example = "admin@email.com")
    private String email;
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
