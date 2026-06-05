package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Identifiants de connexion")
public class LoginRequest {

  @NotBlank
  @Schema(description = "Adresse email ou nom d'utilisateur", example = "jean.dupont@example.com")
  private String identifier;

  @NotBlank
  @Schema(description = "Mot de passe", example = "secure123")
  private String password;
}
