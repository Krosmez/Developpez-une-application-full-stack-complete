package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Sujets", description = "Gestion des thématiques disponibles")
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "Lister tous les sujets disponibles (paginé)")
    @GetMapping
    public ResponseEntity<Page<SubjectResponse>> getAllSubjects(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(subjectService.getAllSubjects(pageable));
    }
}
