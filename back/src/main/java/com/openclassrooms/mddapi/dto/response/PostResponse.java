package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article résumé")
public class PostResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Introduction à Spring Boot")
    private String title;

    @Schema(example = "Dans cet article, nous allons...")
    private String content;
}
