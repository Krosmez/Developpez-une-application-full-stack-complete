package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Auteur résumé")
public class AuthorDto {

  @Schema(example = "1")
  private Long id;

  @Schema(example = "jean_dupont")
  private String username;
}
