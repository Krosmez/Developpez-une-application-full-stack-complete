package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.Subject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

  SubjectResponse toDto(Subject subject);

  List<SubjectResponse> toDtoList(List<Subject> subjects);
}
