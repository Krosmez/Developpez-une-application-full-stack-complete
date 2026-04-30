package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.response.FeedItemResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@Tag(name = "Fil d'actualité", description = "Articles des sujets auxquels l'utilisateur est abonné")
public class FeedController {

    private final PostService postService;

    @Operation(summary = "Récupérer le fil d'actualité de l'utilisateur connecté")
    @GetMapping
    public ResponseEntity<Page<FeedItemResponse>> getFeed(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getFeed(currentUser.getId(), pageable));
    }
}
