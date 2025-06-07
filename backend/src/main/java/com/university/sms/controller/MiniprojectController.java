package com.university.sms.controller;

import com.university.sms.dto.MiniprojectCreateDTO;
import com.university.sms.dto.MiniprojectUpdateDTO;
import com.university.sms.dto.response.MiniprojectResponseDTO;
import com.university.sms.entity.Miniproject;
import com.university.sms.dto.EvaluationDTO;
import com.university.sms.security.CurrentUser;
import com.university.sms.security.UserPrincipal;
import com.university.sms.service.MiniprojectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller REST pour la gestion des miniprojets
 */
@RestController
@RequestMapping("/miniprojects")
@RequiredArgsConstructor
@Tag(name = "Miniprojects", description = "API de gestion des miniprojets")
@SecurityRequirement(name = "bearerAuth")
public class MiniprojectController extends BaseController {

    private final MiniprojectService miniprojectService;

    @Operation(summary = "Créer un nouveau miniprojet")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MiniprojectResponseDTO> createMiniproject(
            @Valid @RequestPart("project") MiniprojectCreateDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.createMiniproject(dto, currentUser.getId(), files);
        return created(response);
    }

    @Operation(summary = "Récupérer un miniprojet par ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<Miniproject> getMiniproject(@PathVariable Long id) {
        Miniproject response = miniprojectService.findById(id);
        return ok(response);
    }

    @Operation(summary = "Mettre à jour un miniprojet")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MiniprojectResponseDTO> updateMiniproject(
            @PathVariable Long id,
            @Valid @RequestBody MiniprojectUpdateDTO dto,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.updateMiniproject(id, dto, currentUser.getId());
        return ok(response);
    }

    @Operation(summary = "Soumettre un miniprojet pour validation")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MiniprojectResponseDTO> submitMiniproject(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.submitMiniproject(id, currentUser.getId());
        return ok(response);
    }

    @Operation(summary = "Valider un miniprojet")
    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MiniprojectResponseDTO> validateMiniproject(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.validateMiniproject(id, currentUser.getId());
        return ok(response);
    }

    @Operation(summary = "Rejeter un miniprojet")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MiniprojectResponseDTO> rejectMiniproject(
            @PathVariable Long id,
            @RequestParam String feedback,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.rejectMiniproject(id, currentUser.getId(), feedback);
        return ok(response);
    }

    @Operation(summary = "Évaluer un miniprojet")
    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MiniprojectResponseDTO> evaluateMiniproject(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationDTO evaluationDTO,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.evaluateMiniproject(
                id, currentUser.getId(), evaluationDTO.getGrade(), evaluationDTO.getFeedback());
        return ok(response);
    }

    @Operation(summary = "Récupérer mes miniprojets (étudiant)")
    @GetMapping("/my-projects")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<MiniprojectResponseDTO>> getMyProjects(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        List<MiniprojectResponseDTO> projects = miniprojectService.findByStudent(currentUser.getId());
        return ok(projects);
    }

    @Operation(summary = "Récupérer les projets supervisés (enseignant)")
    @GetMapping("/supervised")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Page<MiniprojectResponseDTO>> getSupervisedProjects(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<MiniprojectResponseDTO> projects = miniprojectService.findBySupervisor(currentUser.getId(), pageable);
        return ok(projects);
    }

    @Operation(summary = "Récupérer les projets en attente de validation")
    @GetMapping("/pending-validation")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Page<MiniprojectResponseDTO>> getPendingValidation(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<MiniprojectResponseDTO> projects = miniprojectService.findPendingValidation(pageable);
        return ok(projects);
    }

    @Operation(summary = "Ajouter un document à un miniprojet")
    @PostMapping("/{id}/documents")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MiniprojectResponseDTO> addDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        MiniprojectResponseDTO response = miniprojectService.addDocument(id, currentUser.getId(), file);
        return ok(response);
    }

    @Operation(summary = "Supprimer un document d'un miniprojet")
    @DeleteMapping("/{projectId}/documents/{documentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> removeDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        miniprojectService.removeDocument(projectId, documentId, currentUser.getId());
        return noContent();
    }
}
