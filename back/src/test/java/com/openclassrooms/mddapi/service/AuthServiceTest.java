package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.EmailAlreadyExistsException;
import com.openclassrooms.mddapi.exception.InvalidCredentialsException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyExistsException;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("jean@example.com");
        registerRequest.setUsername("jean");
        registerRequest.setPassword("Secure1!");

        loginRequest = new LoginRequest();
        loginRequest.setIdentifier("jean@example.com");
        loginRequest.setPassword("Secure1!");
    }

    @Test
    @DisplayName("register : crée l'utilisateur et retourne un token")
    void register_success() {
        when(userRepository.existsByEmail("jean@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("jean")).thenReturn(false);
        when(passwordEncoder.encode("Secure1!")).thenReturn("hashed");
        User saved = User.builder().id(1L).email("jean@example.com").username("jean").password("hashed").build();
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtService.generateToken(saved)).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("jean@example.com")
                        && u.getUsername().equals("jean")
                        && u.getPassword().equals("hashed")));
    }

    @Test
    @DisplayName("register : échoue si l'email existe déjà")
    void register_emailAlreadyExists() {
        when(userRepository.existsByEmail("jean@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register : échoue si le username existe déjà")
    void register_usernameAlreadyExists() {
        when(userRepository.existsByEmail("jean@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("jean")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login : authentifie et retourne un token")
    void login_success() {
        User user = User.builder().id(1L).email("jean@example.com").username("jean").build();
        when(userRepository.findByEmailOrUsername("jean@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login : échoue avec de mauvais identifiants")
    void login_badCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("login : échoue si l'utilisateur est introuvable après authentification")
    void login_userNotFound() {
        when(userRepository.findByEmailOrUsername("jean@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
