package com.meneses.auth.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    @Schema(description = "Token JWT. Use in header: Authorization: Bearer {token}",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    @Schema(description = "token for JWT renewal",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
