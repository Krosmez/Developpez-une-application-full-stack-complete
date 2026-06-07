package com.openclassrooms.mddapi.integration;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("UserController - tests d'intégration")
class UserControllerIT extends AbstractIntegrationTest {

  @Test
  @DisplayName("GET /users/me : renvoie le profil de l'utilisateur connecté (200)")
  void getCurrentUser_returnsProfile() throws Exception {
    String token = registerAndGetToken("frank@example.com", "frank", "Secure1!");

    mockMvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email", is("frank@example.com")))
           .andExpect(jsonPath("$.username", is("frank")));
  }

  @Test
  @DisplayName("GET /users/me : 403 sans token d'authentification")
  void getCurrentUser_withoutToken_isForbidden() throws Exception {
    mockMvc.perform(get("/users/me")).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("GET /users/{id} : renvoie le profil demandé (200)")
  void getUserById_returnsProfile() throws Exception {
    String token = registerAndGetToken("grace@example.com", "grace", "Secure1!");
    long id = getCurrentUserId(token);

    mockMvc.perform(get("/users/" + id).header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is("grace")));
  }

  @Test
  @DisplayName("GET /users/{id} : 404 si l'utilisateur est introuvable")
  void getUserById_notFound_returns404() throws Exception {
    String token = registerAndGetToken("heidi@example.com", "heidi", "Secure1!");

    mockMvc.perform(get("/users/999999").header("Authorization", "Bearer " + token)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PUT /users/{id} : met à jour le profil de l'utilisateur connecté (200)")
  void updateUser_updatesOwnProfile() throws Exception {
    String token = registerAndGetToken("ivan@example.com", "ivan", "Secure1!");
    long id = getCurrentUserId(token);

    UpdateUserRequest update = new UpdateUserRequest();
    update.setUsername("ivan_updated");
    update.setBio("Nouvelle bio");

    mockMvc.perform(put("/users/" + id).header("Authorization", "Bearer " + token)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(update)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is("ivan_updated")))
           .andExpect(jsonPath("$.bio", is("Nouvelle bio")));
  }

  @Test
  @DisplayName("PUT /users/{id} : 409 si le nouvel email est déjà pris")
  void updateUser_duplicateEmail_returns409() throws Exception {
    registerAndGetToken("judy@example.com", "judy", "Secure1!");
    String token = registerAndGetToken("ken@example.com", "ken", "Secure1!");
    long id = getCurrentUserId(token);

    UpdateUserRequest update = new UpdateUserRequest();
    update.setEmail("judy@example.com");

    mockMvc.perform(put("/users/" + id).header("Authorization", "Bearer " + token)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(update))).andExpect(status().isConflict());
  }
}
