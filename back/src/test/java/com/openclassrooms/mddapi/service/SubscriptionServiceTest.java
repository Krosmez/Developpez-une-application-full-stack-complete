package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.SubjectMapper;
import com.openclassrooms.mddapi.repository.SubjectRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService")
class SubscriptionServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;
    private Subject subject;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("jean").subscriptions(new ArrayList<>()).build();
        subject = Subject.builder().id(2L).name("Java").build();
    }

    @Test
    @DisplayName("getSubscriptions : retourne les abonnements de l'utilisateur")
    void getSubscriptions_success() {
        user.getSubscriptions().add(subject);
        List<SubjectResponse> dtos = List.of(new SubjectResponse(2L, "Java", null));
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectMapper.toDtoList(user.getSubscriptions())).thenReturn(dtos);

        assertThat(subscriptionService.getSubscriptions(1L)).isEqualTo(dtos);
    }

    @Test
    @DisplayName("getSubscriptions : lève ResourceNotFound si utilisateur absent")
    void getSubscriptions_userNotFound() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.getSubscriptions(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("subscribe : ajoute l'abonnement")
    void subscribe_success() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(subjectMapper.toDtoList(anyList())).thenReturn(List.of(new SubjectResponse(2L, "Java", null)));

        subscriptionService.subscribe(1L, 2L);

        assertThat(user.getSubscriptions()).containsExactly(subject);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("subscribe : conflit si déjà abonné")
    void subscribe_alreadySubscribed() {
        user.getSubscriptions().add(subject);
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));

        assertThatThrownBy(() -> subscriptionService.subscribe(1L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("subscribe : ResourceNotFound si utilisateur absent")
    void subscribe_userNotFound() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.subscribe(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("subscribe : ResourceNotFound si sujet absent")
    void subscribe_subjectNotFound() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.subscribe(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("unsubscribe : retire l'abonnement")
    void unsubscribe_success() {
        user.getSubscriptions().add(subject);
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(subjectMapper.toDtoList(anyList())).thenReturn(List.of());

        subscriptionService.unsubscribe(1L, 2L);

        assertThat(user.getSubscriptions()).isEmpty();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("unsubscribe : BadRequest si non abonné")
    void unsubscribe_notSubscribed() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));

        assertThatThrownBy(() -> subscriptionService.unsubscribe(1L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("unsubscribe : ResourceNotFound si utilisateur absent")
    void unsubscribe_userNotFound() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("unsubscribe : ResourceNotFound si sujet absent")
    void unsubscribe_subjectNotFound() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(user));
        when(subjectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
