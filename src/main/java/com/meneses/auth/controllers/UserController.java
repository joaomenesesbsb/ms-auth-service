package com.meneses.auth.controllers;

import com.meneses.auth.dto.RoleRequest;
import com.meneses.auth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public String userInfo() {
        return "Informações do usuário";
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRole(
            @PathVariable Long id,
            @RequestBody RoleRequest request) {
        userService.addRoleToUser(id, request.getRoleName());
        return ResponseEntity.ok("Role adicionada");
    }
}
