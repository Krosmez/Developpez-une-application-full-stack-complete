package com.openclassrooms.mddapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Données pour créer un article")
public class CreatePostRequest {

    @NotBlank
    @Schema(description = "Titre de l'article", example = "Introduction à Spring Boot")
    private String title;

    @NotBlank
    @Schema(description = "Contenu de l'article", example = "Dans cet article, nous allons...")
    private String content;

    @NotNull
    @Schema(description = "Identifiant du sujet associé", example = "1")
    private Long subjectId;
}
