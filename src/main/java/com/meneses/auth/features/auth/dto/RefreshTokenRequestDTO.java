package com.meneses.auth.features.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
