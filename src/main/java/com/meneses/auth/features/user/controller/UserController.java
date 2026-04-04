package com.meneses.auth.features.user.controller;

import com.meneses.auth.features.user.dto.RoleRequest;
import com.meneses.auth.features.user.dto.UserResponse;
import com.meneses.auth.features.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuario")
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController implements UserControllerImpl {

    @Autowired
    private UserService userService;

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserResponse request){
        UserResponse response = userService.update(id,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<UserResponse>> findAll(
            @RequestParam(value = "email", defaultValue = "")
            String email,
            Pageable pageable) {
        Page<UserResponse> list = userService.findAll(email,pageable);
        return ResponseEntity.ok(list);
    }

    @Override
    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> addRole(
            @PathVariable Long id,
            @RequestBody RoleRequest request) {
        userService.addRoleToUser(id, request.getRoleName());
        return ResponseEntity.noContent().build();
    }
}
