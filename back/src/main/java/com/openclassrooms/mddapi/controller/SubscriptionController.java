package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler.ErrorResponse;
import com.openclassrooms.mddapi.service.SubscriptionService;
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
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Abonnements", description = "Gestion des abonnements aux thèmes")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "S'abonner à un sujet")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Abonnement effectué, retourne la liste mise à jour"),
        @ApiResponse(responseCode = "404", description = "Sujet introuvable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Déjà abonné à ce sujet",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{subjectId}")
    public ResponseEntity<List<SubjectResponse>> subscribe(
            @PathVariable Long subjectId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(subscriptionService.subscribe(currentUser.getId(), subjectId));
    }

    @Operation(summary = "Se désabonner d'un sujet")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Désabonnement effectué, retourne la liste mise à jour"),
        @ApiResponse(responseCode = "400", description = "Non abonné à ce sujet",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Sujet introuvable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<List<SubjectResponse>> unsubscribe(
            @PathVariable Long subjectId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(subscriptionService.unsubscribe(currentUser.getId(), subjectId));
    }
}
