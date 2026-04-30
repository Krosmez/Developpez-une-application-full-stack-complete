package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Élément du fil d'actualité")
public class FeedItemResponse {

    private PostSummary post;

    private SubjectResponse subject;
}
