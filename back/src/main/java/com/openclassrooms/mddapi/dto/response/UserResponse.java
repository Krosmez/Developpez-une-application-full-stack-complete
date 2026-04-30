package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Informations de base de l'utilisateur créé")
public class UserResponse {

    @Schema(description = "Identifiant unique", example = "1")
    private Long id;

    @Schema(description = "Adresse email", example = "jean.dupont@example.com")
    private String email;
}
