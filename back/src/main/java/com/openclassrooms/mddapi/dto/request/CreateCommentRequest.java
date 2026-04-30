package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Données pour ajouter un commentaire")
public class CreateCommentRequest {

    @NotBlank
    @Schema(description = "Contenu du commentaire", example = "Super article !")
    private String content;
}
