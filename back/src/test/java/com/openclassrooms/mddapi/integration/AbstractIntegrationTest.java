package com.openclassrooms.mddapi.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Base commune aux tests d'intégration : démarre le contexte Spring complet,
 * expose {@link MockMvc} et la base H2 en mémoire (cf. src/test/resources/application.properties).
 * Chaque test est transactionnel et rollback automatiquement à la fin.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class AbstractIntegrationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  /**
   * Inscrit un utilisateur via l'API et retourne le token JWT renvoyé.
   */
  protected String registerAndGetToken(String email, String username, String password) throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail(email);
    request.setUsername(username);
    request.setPassword(password);

    String body = mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                         .andReturn()
                         .getResponse()
                         .getContentAsString();

    return objectMapper.readTree(body).get("token").asText();
  }

  /**
   * Récupère l'identifiant de l'utilisateur connecté via GET /users/me.
   */
  protected long getCurrentUserId(String token) throws Exception {
    String body = mockMvc.perform(get("/users/me").header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
    JsonNode node = objectMapper.readTree(body);
    return node.get("id").asLong();
  }
}
