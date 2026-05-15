package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Données d'inscription d'un nouvel utilisateur")
public class RegisterRequest {

    @NotBlank
    @Email
    @Schema(description = "Adresse email", example = "jean.dupont@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Nom d'utilisateur unique", example = "jean_dupont")
    private String username;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
        message = "Password must be at least 8 characters and contain at least one digit, one lowercase, one uppercase, and one special character"
    )
    @Schema(description = "Mot de passe (8 car. min, 1 chiffre, 1 minuscule, 1 majuscule, 1 caractère spécial)", example = "Secure1!")
    private String password;

}
