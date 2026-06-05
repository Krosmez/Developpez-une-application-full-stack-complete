package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article détaillé avec auteur et commentaires")
public class PostDetailResponse {

  @Schema(example = "1")
  private Long id;

  @Schema(example = "Introduction à Spring Boot")
  private String title;

  @Schema(example = "Dans cet article, nous allons...")
  private String content;

  private AuthorDto author;

  private SubjectResponse subject;

  private LocalDateTime createdAt;

  private List<CommentResponse> comments;
}
