package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.Subject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SubjectMapper")
class SubjectMapperTest {

    private final SubjectMapper mapper = Mappers.getMapper(SubjectMapper.class);

    @Test
    @DisplayName("toDto : mappe les champs du sujet")
    void toDto() {
        Subject subject = Subject.builder().id(1L).name("Java").description("desc").build();

        SubjectResponse dto = mapper.toDto(subject);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Java");
        assertThat(dto.getDescription()).isEqualTo("desc");
    }

    @Test
    @DisplayName("toDtoList : mappe une liste de sujets")
    void toDtoList() {
        List<SubjectResponse> dtos = mapper.toDtoList(List.of(
                Subject.builder().id(1L).name("Java").build(),
                Subject.builder().id(2L).name("Angular").build()));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(SubjectResponse::getName).containsExactly("Java", "Angular");
    }
}
