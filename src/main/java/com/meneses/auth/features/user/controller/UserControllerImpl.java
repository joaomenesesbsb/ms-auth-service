package com.meneses.auth.features.user.controller;

import com.meneses.auth.features.user.dto.RoleRequest;
import com.meneses.auth.features.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Usuários", description = "Gerenciamento de usuários e permissões")
public interface UserControllerImpl {

    @Operation(summary = "Busca usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<UserResponse> findById(@PathVariable Long id);

    @Operation(summary = "Atualiza dados do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso na atualização"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UserResponse request);

    @Operation(summary = "Lista usuários paginados")
    ResponseEntity<Page<UserResponse>> findAll(@RequestParam String email, Pageable pageable);

    @Operation(summary = "Adiciona Role ao usuário")
    ResponseEntity<Void> addRole(@PathVariable Long id, @RequestBody RoleRequest request);
}
