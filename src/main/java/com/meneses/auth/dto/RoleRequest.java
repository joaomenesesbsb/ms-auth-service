package com.meneses.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @Schema(description = "Nome do role a ser adicionado", example = "ROLE_ADMIN")
    private String roleName;
}
