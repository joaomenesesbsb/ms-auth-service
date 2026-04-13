package com.meneses.auth.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.user.controller.UserController;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.features.user.service.UserService;
import com.meneses.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "ADMIN")
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserResponseDTO userDTO;
    private final Long VALID_ID = 1L;
    private final Long INVALID_ID = 99L;

    @BeforeEach
    void setUp() {
        userDTO = new UserResponseDTO("teste@email.com");
    }

    @Nested
    @DisplayName("Testes de findById")
    class findById {

        @Test
        @DisplayName("Deve retornar 200 e o DTO quando o usuário existir")
        void shouldReturnOk_whenUserExists() throws Exception {
            when(userService.findById(VALID_ID)).thenReturn(userDTO);

            mockMvc.perform(get("/users/{id}", VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("teste@email.com"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando o usuário não for encontrado")
        void shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
            when(userService.findById(INVALID_ID)).thenThrow(new ResourceNotFoundException("Not Found"));

            mockMvc.perform(get("/users/{id}", INVALID_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

    }

}
