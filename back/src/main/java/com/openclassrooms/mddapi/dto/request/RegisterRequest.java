package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Données d'inscription d'un nouvel utilisateur")
public class RegisterRequest {

    @NotBlank
    @Email
    @Schema(description = "Adresse email", example = "jean.dupont@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Mot de passe (8 caractères minimum)", example = "secure123")
    private String password;

    @NotBlank
    @Schema(description = "Prénom", example = "Jean")
    private String firstName;

    @NotBlank
    @Schema(description = "Nom de famille", example = "Dupont")
    private String lastName;
}
