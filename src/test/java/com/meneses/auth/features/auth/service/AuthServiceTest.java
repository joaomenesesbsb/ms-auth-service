package com.meneses.auth.features.auth.service;

import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.security.JwtService;
import com.meneses.auth.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService blacklistService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_USER");

        user = new User();
        user.setEmail("teste@email.com");
        user.setPassword("senhaCriptografada");
        user.getRoles().add(role);
    }

    @Nested
    class Login{
        @Test
        @DisplayName("Deve fazer login com sucesso quando credenciais forem válidas")
        void shouldReturnLoginResponse_whenCredentialsAreValid() {

            LoginRequestDTO request = new LoginRequestDTO("teste@email.com", "senha123");

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(user)).thenReturn("token-jwt");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

            LoginResponseDTO response = authService.login(request);

            assertNotNull(response);
            assertEquals("token-jwt", response.getToken());
            assertEquals("refresh-token", response.getRefreshToken());
            verify(jwtService, times(1)).generateToken(user);
        }

        @Test
        void shouldThrowException_whenPasswordIsIncorrect() {

            LoginRequestDTO request = new LoginRequestDTO("test@email.com", "wrongPassword");

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.login(request);
            });

            assertEquals("Invalid password", exception.getMessage());
        }

        @Test
        void shouldThrowResourceNotFoundException_whenUserDoesNotExist() {

            LoginRequestDTO request = new LoginRequestDTO("notExist@email.com", "123");
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                authService.login(request);
            });
        }
    }

    @Nested
    class Register {
        @Test
        void shouldThrowException_whenEmailIsAlreadyRegistered() {

            RegisterRequestDTO request = new RegisterRequestDTO("test@email.com", "senha123");
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.register(request);
            });

            assertEquals("Email already registered", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Logout {

        @Test
        void shouldBlacklistToken_whenValid() {

            HttpServletRequest request = mock(HttpServletRequest.class);
            String token = "valid.jwt.token";
            long expirationSeconds = 3600L;

            when(jwtService.extractToken(request)).thenReturn(token);
            when(jwtService.getExpirationInSeconds(token)).thenReturn(expirationSeconds);

            authService.logout(request);

            verify(jwtService, times(1)).getExpirationInSeconds(token);
            verify(blacklistService, times(1)).blacklistToken(token, expirationSeconds);
        }

        @Test
        void shouldNotBlacklist_whenTokenIsNull() {
            authService.logout(null);

            verifyNoInteractions(jwtService);
            verifyNoInteractions(blacklistService);
        }
    }

}