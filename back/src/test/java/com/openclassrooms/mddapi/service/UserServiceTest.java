package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.UserProfileResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.EmailAlreadyExistsException;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyExistsException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserProfileResponse profile;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("jean@example.com").username("jean").password("hashed").build();
        profile = new UserProfileResponse(1L, "jean@example.com", "jean", "bio");
    }

    @Test
    @DisplayName("getUserById : retourne le profil")
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toProfileDto(user)).thenReturn(profile);

        assertThat(userService.getUserById(1L)).isEqualTo(profile);
    }

    @Test
    @DisplayName("getUserById : lève ResourceNotFound si absent")
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getUserByUsername : retourne le profil")
    void getUserByUsername_found() {
        when(userRepository.findByEmailOrUsername("jean")).thenReturn(Optional.of(user));
        when(userMapper.toProfileDto(user)).thenReturn(profile);

        assertThat(userService.getUserByUsername("jean")).isEqualTo(profile);
    }

    @Test
    @DisplayName("updateUser : met à jour le profil de l'utilisateur courant")
    void updateUser_success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@example.com");
        request.setUsername("newname");
        request.setPassword("NewSecure1!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("newname", 1L)).thenReturn(false);
        when(passwordEncoder.encode("NewSecure1!")).thenReturn("newHashed");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toProfileDto(user)).thenReturn(profile);

        UserProfileResponse result = userService.updateUser(1L, request, user);

        assertThat(result).isEqualTo(profile);
        verify(userMapper).updateUserFromRequest(request, user);
        verify(passwordEncoder).encode("NewSecure1!");
        assertThat(user.getPassword()).isEqualTo("newHashed");
    }

    @Test
    @DisplayName("updateUser : ignore le mot de passe vide")
    void updateUser_blankPasswordIgnored() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("   ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toProfileDto(user)).thenReturn(profile);

        userService.updateUser(1L, request, user);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("updateUser : interdit la modification d'un autre profil")
    void updateUser_forbidden() {
        User other = User.builder().id(2L).build();
        UpdateUserRequest request = new UpdateUserRequest();

        assertThatThrownBy(() -> userService.updateUser(1L, request, other))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.FORBIDDEN);

        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("updateUser : lève ResourceNotFound si l'utilisateur n'existe pas")
    void updateUser_notFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, request, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateUser : échoue si l'email est déjà pris")
    void updateUser_emailConflict() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("taken@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request, user))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @DisplayName("updateUser : échoue si le username est déjà pris")
    void updateUser_usernameConflict() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("taken");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndIdNot("taken", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request, user))
                .isInstanceOf(UsernameAlreadyExistsException.class);
    }
}
