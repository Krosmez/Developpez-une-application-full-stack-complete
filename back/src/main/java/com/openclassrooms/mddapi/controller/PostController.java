package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;
import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.dto.response.FeedItemResponse;
import com.openclassrooms.mddapi.dto.response.PostDetailResponse;
import com.openclassrooms.mddapi.dto.response.PostResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler.ErrorResponse;
import com.openclassrooms.mddapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Création et consultation des articles")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Créer un nouvel article")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Article créé",
            content = @Content(schema = @Schema(implementation = PostResponse.class))),
        @ApiResponse(responseCode = "404", description = "Sujet introuvable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/v1/posts")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, currentUser));
    }

    @Operation(summary = "Récupérer un article par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article trouvé",
            content = @Content(schema = @Schema(implementation = PostDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Article introuvable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @Operation(summary = "Ajouter un commentaire à un article")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Commentaire ajouté",
            content = @Content(schema = @Schema(implementation = CommentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Article introuvable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/v1/posts/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addComment(id, request, currentUser));
    }

    @Operation(summary = "Récupérer le fil d'actualité de l'utilisateur connecté")
    @GetMapping("/api/v1/feed")
    public ResponseEntity<Page<FeedItemResponse>> getFeed(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getFeed(currentUser.getId(), pageable));
    }
}
