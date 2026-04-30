package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Données de mise à jour du profil (tous les champs sont optionnels)")
public class UpdateUserRequest {

    @Schema(description = "Prénom", example = "Jean")
    private String firstName;

    @Schema(description = "Nom de famille", example = "Dupont")
    private String lastName;

    @Schema(description = "Biographie", example = "Développeur Full-Stack")
    private String bio;
}
