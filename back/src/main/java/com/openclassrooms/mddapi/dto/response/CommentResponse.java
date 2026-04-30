package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Commentaire")
public class CommentResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Super article !")
    private String content;

    private AuthorDto author;
}
