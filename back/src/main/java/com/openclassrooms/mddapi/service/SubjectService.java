package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.mapper.SubjectMapper;
import com.openclassrooms.mddapi.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public Page<SubjectResponse> getAllSubjects(Pageable pageable) {
        return subjectRepository.findAll(pageable).map(subjectMapper::toDto);
    }
}
