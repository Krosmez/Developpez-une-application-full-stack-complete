package com.openclassrooms.mddapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sujet / thématique")
public class SubjectResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Java")
    private String name;

    @Schema(example = "Tout ce qui concerne l'écosystème Java")
    private String description;
}
