package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Données de mise à jour du profil (tous les champs sont optionnels)")
public class UpdateUserRequest {

  @Email(message = "Invalid email format")
  @Schema(description = "Nouvelle adresse email", example = "jean.dupont@example.com")
  private String email;

  @Schema(description = "Nouveau nom d'utilisateur", example = "jean_dupont")
  private String username;

  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$", message = "Password must be at " +
      "least 8 characters and contain at least one digit, one lowercase, one uppercase, and one special character")
  @Schema(description = "Nouveau mot de passe", example = "Secure1!")
  private String password;

  @Schema(description = "Biographie", example = "Développeur Full-Stack")
  private String bio;
}
