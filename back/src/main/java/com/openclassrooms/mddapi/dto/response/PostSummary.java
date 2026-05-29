package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article résumé pour le fil d'actualité")
public class PostSummary {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Introduction à Spring Boot")
    private String title;

    @Schema(example = "Lorem ipsum...")
    private String content;

    private AuthorDto author;

    private LocalDateTime createdAt;
}
