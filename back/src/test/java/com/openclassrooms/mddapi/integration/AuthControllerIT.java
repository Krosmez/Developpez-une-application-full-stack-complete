package com.openclassrooms.mddapi.integration;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AuthController - tests d'intégration")
class AuthControllerIT extends AbstractIntegrationTest {

  @Test
  @DisplayName("POST /auth/register : crée l'utilisateur et renvoie un token (201)")
  void register_returns201AndToken() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("alice@example.com");
    request.setUsername("alice");
    request.setPassword("Secure1!");

    mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.token", notNullValue()));
  }

  @Test
  @DisplayName("POST /auth/register : 409 si l'email existe déjà")
  void register_duplicateEmail_returns409() throws Exception {
    registerAndGetToken("bob@example.com", "bob", "Secure1!");

    RegisterRequest duplicate = new RegisterRequest();
    duplicate.setEmail("bob@example.com");
    duplicate.setUsername("bob2");
    duplicate.setPassword("Secure1!");

    mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(duplicate)))
           .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("POST /auth/register : 400 si le mot de passe ne respecte pas le format")
  void register_invalidPassword_returns400() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("carol@example.com");
    request.setUsername("carol");
    request.setPassword("weak");

    mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /auth/login : authentifie et renvoie un token (200)")
  void login_returns200AndToken() throws Exception {
    registerAndGetToken("dave@example.com", "dave", "Secure1!");

    LoginRequest login = new LoginRequest();
    login.setIdentifier("dave@example.com");
    login.setPassword("Secure1!");

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(login)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.token", notNullValue()));
  }

  @Test
  @DisplayName("POST /auth/login : 401 avec un mauvais mot de passe")
  void login_badCredentials_returns401() throws Exception {
    registerAndGetToken("erin@example.com", "erin", "Secure1!");

    LoginRequest login = new LoginRequest();
    login.setIdentifier("erin@example.com");
    login.setPassword("WrongPass1!");

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(login)))
           .andExpect(status().isUnauthorized());
  }
}
