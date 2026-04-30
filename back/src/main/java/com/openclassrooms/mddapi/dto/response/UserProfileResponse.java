package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Profil complet d'un utilisateur")
public class UserProfileResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "jean.dupont@example.com")
    private String email;

    @Schema(example = "Jean")
    private String firstName;

    @Schema(example = "Dupont")
    private String lastName;

    @Schema(example = "Développeur Java")
    private String bio;
}
