package com.openclassrooms.mddapi.integration;

import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.repository.SubjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Subjects & Subscriptions - tests d'intégration")
class SubscriptionControllerIT extends AbstractIntegrationTest {

  @Autowired
  private SubjectRepository subjectRepository;

  private Subject seedSubject(String name) {
    return subjectRepository.save(Subject.builder().name(name).description("desc " + name).build());
  }

  @Test
  @DisplayName("GET /subjects : liste les sujets triés par nom (200)")
  void getAllSubjects_returnsList() throws Exception {
    seedSubject("Java");
    seedSubject("Angular");
    String token = registerAndGetToken("leo@example.com", "leo", "Secure1!");

    mockMvc.perform(get("/subjects").header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[0].name", is("Angular")))
           .andExpect(jsonPath("$[1].name", is("Java")));
  }

  @Test
  @DisplayName("Flux complet : s'abonner, lister ses abonnements, se désabonner")
  void subscribe_listAndUnsubscribe() throws Exception {
    Subject subject = seedSubject("DevOps");
    String token = registerAndGetToken("mia@example.com", "mia", "Secure1!");

    // S'abonner -> la liste renvoyée contient le sujet
    mockMvc.perform(post("/subscriptions/" + subject.getId()).header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(1)))
           .andExpect(jsonPath("$[0].name", is("DevOps")));

    // Lister ses abonnements
    mockMvc.perform(get("/users/me/subscriptions").header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(1)))
           .andExpect(jsonPath("$[0].name", is("DevOps")));

    // Se désabonner -> liste vide
    mockMvc.perform(delete("/subscriptions/" + subject.getId()).header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("POST /subscriptions/{id} : 404 si le sujet n'existe pas")
  void subscribe_unknownSubject_returns404() throws Exception {
    String token = registerAndGetToken("nina@example.com", "nina", "Secure1!");

    mockMvc.perform(post("/subscriptions/999999").header("Authorization", "Bearer " + token)).andExpect(status().isNotFound());
  }
}
