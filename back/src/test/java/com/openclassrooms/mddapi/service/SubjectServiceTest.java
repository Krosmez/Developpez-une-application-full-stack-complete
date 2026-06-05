package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.mapper.SubjectMapper;
import com.openclassrooms.mddapi.repository.SubjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectService")
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    @DisplayName("getAllSubjects : retourne les sujets triés et mappés")
    void getAllSubjects() {
        Subject java = Subject.builder().id(1L).name("Java").build();
        Subject angular = Subject.builder().id(2L).name("Angular").build();
        SubjectResponse javaDto = new SubjectResponse(1L, "Java", null);
        SubjectResponse angularDto = new SubjectResponse(2L, "Angular", null);

        when(subjectRepository.findAll(Sort.by("name").ascending())).thenReturn(List.of(angular, java));
        when(subjectMapper.toDto(angular)).thenReturn(angularDto);
        when(subjectMapper.toDto(java)).thenReturn(javaDto);

        List<SubjectResponse> result = subjectService.getAllSubjects();

        assertThat(result).containsExactly(angularDto, javaDto);
    }
}
