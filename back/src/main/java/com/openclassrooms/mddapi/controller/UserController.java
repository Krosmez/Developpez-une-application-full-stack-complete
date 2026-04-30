package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.response.PostResponse;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler.ErrorResponse;
import com.openclassrooms.mddapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des profils utilisateurs")
public class UserController {

    private final PostService postService;

    @Operation(summary = "Lister les articles publiés par un utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des articles"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostsByUserId(id));
    }
}
