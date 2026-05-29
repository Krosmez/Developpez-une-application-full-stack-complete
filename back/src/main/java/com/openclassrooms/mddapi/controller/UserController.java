package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.PostResponse;
import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.dto.response.UserProfileResponse;
import com.openclassrooms.mddapi.service.SubscriptionService;
import jakarta.validation.Valid;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler.ErrorResponse;
import com.openclassrooms.mddapi.service.PostService;
import com.openclassrooms.mddapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des profils utilisateurs")
public class UserController {

  private final UserService userService;
  private final PostService postService;
  private final SubscriptionService subscriptionService;

  @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
  @ApiResponse(responseCode = "200", description = "Profil de l'utilisateur connecté", content = @Content(schema = @Schema(implementation =
      UserProfileResponse.class)))
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal User currentUser) {
    return ResponseEntity.ok(userService.getUserByUsername(currentUser.getUsername()));
  }

  @Operation(summary = "Lister les abonnements de l'utilisateur connecté")
  @ApiResponse(responseCode = "200", description = "Liste des sujets auxquels l'utilisateur est abonné")
  @GetMapping("/me/subscriptions")
  public ResponseEntity<List<SubjectResponse>> getCurrentUserSubscriptions(@AuthenticationPrincipal User currentUser) {
    return ResponseEntity.ok(subscriptionService.getSubscriptions(currentUser.getId()));
  }

  @Operation(summary = "Récupérer le profil d'un utilisateur par son id")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Profil trouvé", content = @Content(schema = @Schema(implementation =
      UserProfileResponse.class))), @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content(schema =
  @Schema(implementation = ErrorResponse.class)))})
  @GetMapping("/{id}")
  public ResponseEntity<UserProfileResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @Operation(summary = "Mettre à jour le profil de l'utilisateur connecté")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Profil mis à jour", content = @Content(schema = @Schema(implementation =
      UserProfileResponse.class))), @ApiResponse(responseCode = "403", description = "Modification d'un autre profil interdite", content =
  @Content(schema = @Schema(implementation = ErrorResponse.class))), @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class))), @ApiResponse(responseCode = "409", description =
      "Email ou nom d'utilisateur déjà utilisé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PutMapping("/{id}")
  public ResponseEntity<UserProfileResponse> updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserRequest request,
      @AuthenticationPrincipal User currentUser
  ) {
    return ResponseEntity.ok(userService.updateUser(id, request, currentUser));
  }

  @Operation(summary = "Lister les articles publiés par un utilisateur")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Liste des articles"), @ApiResponse(responseCode = "404", description =
      "Utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @GetMapping("/{id}/posts")
  public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long id) {
    return ResponseEntity.ok(postService.getPostsByUserId(id));
  }
}
