package com.meneses.auth.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.user.controller.UserController;
import com.meneses.auth.features.user.dto.UserRequestDTO;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    private UserResponseDTO response;
    private UserRequestDTO request;
    private final Long VALID_ID = 1L;
    private final Long INVALID_ID = 99L;

    @BeforeEach
    void setUp() {
        response = new UserResponseDTO("teste@email.com");
        request = new UserRequestDTO("teste@email.com", Collections.singletonList("ROLE_USER"));
    }

    @Nested
    @DisplayName("Testes de findById")
    class findById {

        @Test
        @DisplayName("Deve retornar 200 e o DTO quando o usuário existir")
        void shouldReturnOk_whenUserExists() throws Exception {
            when(userService.findById(VALID_ID)).thenReturn(response);

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

    @Nested
    @DisplayName("Testes de Update")
    class Update {

        @Test
        @DisplayName("Deve retornar 200 e o DTO atualizado quando o ID existir e o request for válido")
        void shouldReturnOk_whenUpdateIsValid() throws Exception {

            when(userService.update(eq(VALID_ID), any(UserRequestDTO.class))).thenReturn(response);

            mockMvc.perform(put("/users/{id}", VALID_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(response.getEmail()));
        }

        @Test
        @DisplayName("Deve retornar 400 quando o request for inválido (Bean Validation)")
        void shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

            UserRequestDTO invalidRequest = new UserRequestDTO("", Collections.singletonList(""));

            mockMvc.perform(put("/users/{id}", VALID_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 404 quando o usuário não for encontrado para update")
        void shouldReturnNotFound_whenIdDoesNotExist() throws Exception {

            when(userService.update(eq(INVALID_ID), any(UserRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Not Found"));

            mockMvc.perform(put("/users/{id}",INVALID_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserRequestDTO("email@teste.com", Collections.singletonList("123")))))
                    .andExpect(status().isNotFound());
        }
    }

}
