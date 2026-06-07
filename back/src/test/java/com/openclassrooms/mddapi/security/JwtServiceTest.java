package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtService")
class JwtServiceTest {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private JwtService jwtService;
    private UserDetails user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 86_400_000L);
        user = User.builder().id(1L).username("jean").email("jean@example.com").password("pwd").build();
    }

    @Test
    @DisplayName("generateToken / extractUsername : round-trip du sujet")
    void generateAndExtractUsername() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("jean");
    }

    @Test
    @DisplayName("isTokenValid : vrai pour un token valide et le bon utilisateur")
    void isTokenValid_true() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid : faux si le username ne correspond pas")
    void isTokenValid_wrongUser() {
        String token = jwtService.generateToken(user);
        UserDetails other = User.builder().username("paul").build();

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid : un token expiré est rejeté (ExpiredJwtException)")
    void isTokenValid_expired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String expiredToken = jwtService.generateToken(user);

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, user))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
