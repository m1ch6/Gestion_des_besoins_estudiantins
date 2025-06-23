package com.university.sms.controller;

import com.university.sms.dto.*;
import com.university.sms.dto.response.ThesisResponseDTO;
import com.university.sms.entity.Thesis;
import com.university.sms.security.CurrentUser;
import com.university.sms.security.UserPrincipal;
import com.university.sms.service.ThesisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import java.util.List;

/**
 * Controller REST pour la gestion des mémoires
 */
@RestController
@RequestMapping("/theses")
@RequiredArgsConstructor
@Tag(name = "Theses", description = "API de gestion des mémoires")
public class ThesisController extends BaseController {

    private final ThesisService thesisService;

    @Operation(summary = "Créer un nouveau mémoire")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ThesisResponseDTO> createThesis(
            @Valid @RequestPart("thesis") ThesisCreateDTO dto,
            @RequestPart("file") MultipartFile file,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO response = thesisService.createThesis(dto, currentUser.getId(), file);
        return created(response);
    }

    @Operation(summary = "Récupérer un mémoire par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Thesis> getThesis(@PathVariable Long id) {
        Thesis response = thesisService.findById(id);
        return ok(response);
    }

    @Operation(summary = "Soumettre un mémoire")
    @PostMapping("/{id}/submit")
    public ResponseEntity<ThesisResponseDTO> submitThesis(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO response = thesisService.submitThesis(id, currentUser.getId());
        return ok(response);
    }

    @Operation(summary = "Mettre à jour la version d'un mémoire")
    @PostMapping("/{id}/update-version")
    public ResponseEntity<ThesisResponseDTO> updateThesisVersion(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO response = thesisService.updateThesisVersion(id, currentUser.getId(), file);
        return ok(response);
    }

    @Operation(summary = "Mettre un mémoire en révision")
    @PostMapping("/{id}/under-review")
    public ResponseEntity<ThesisResponseDTO> putThesisUnderReview(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO response = thesisService.putThesisUnderReview(id, currentUser.getId());
        return ok(response);
    }

    @Operation(summary = "Valider un mémoire")
    @PostMapping("/{id}/validate")
    public ResponseEntity<ThesisResponseDTO> validateThesis(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO response = thesisService.validateThesis(id, currentUser.getId());
        return ok(response);
    }

    @Operation(summary = "Rejeter un mémoire")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ThesisResponseDTO> rejectThesis(
            @PathVariable Long id,
            @RequestParam String feedback,
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO response = thesisService.rejectThesis(id, currentUser.getId(), feedback);
        return ok(response);
    }

    @Operation(summary = "Programmer une soutenance")
    @PostMapping("/{id}/schedule-defense")
    public ResponseEntity<ThesisResponseDTO> scheduleDefense(
            @PathVariable Long id,
            @Valid @RequestBody DefenseScheduleDTO scheduleDTO) {

        ThesisResponseDTO response = thesisService.scheduleDefense(id, scheduleDTO);
        return ok(response);
    }

    @Operation(summary = "Enregistrer le résultat d'une soutenance")
    @PostMapping("/{id}/defense-result")
    public ResponseEntity<ThesisResponseDTO> recordDefenseResult(
            @PathVariable Long id,
            @Valid @RequestBody DefenseResultDTO resultDTO) {

        ThesisResponseDTO response = thesisService.recordDefenseResult(
                id, resultDTO.getGrade(), resultDTO.getFeedback());
        return ok(response);
    }

    @Operation(summary = "Récupérer mon mémoire (étudiant)")
    @GetMapping("/my-thesis")
    public ResponseEntity<ThesisResponseDTO> getMyThesis(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser) {

        ThesisResponseDTO thesis = thesisService.findByStudent(currentUser.getId());
        return ok(thesis);
    }

    @Operation(summary = "Récupérer les mémoires supervisés (enseignant)")
    @GetMapping("/supervised")
    public ResponseEntity<Page<ThesisResponseDTO>> getSupervisedTheses(
            @Parameter(hidden = true) @CurrentUser UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ThesisResponseDTO> theses = thesisService.findBySupervisor(currentUser.getId(), pageable);
        return ok(theses);
    }

    @Operation(summary = "Rechercher des mémoires par mot-clé")
    @GetMapping("/search")
    public ResponseEntity<List<ThesisResponseDTO>> searchByKeyword(@RequestParam String keyword) {
        List<ThesisResponseDTO> theses = thesisService.searchByKeyword(keyword);
        return ok(theses);
    }

    @Operation(summary = "Récupérer les mémoires en attente de validation")
    @GetMapping("/pending-validation")
    public ResponseEntity<List<ThesisResponseDTO>> getPendingValidation() {
        List<ThesisResponseDTO> theses = thesisService.findPendingValidation();
        return ok(theses);
    }

    // ------------------------------------------------------------
    // Télécharger le document principal d’un mémoire
    // ------------------------------------------------------------
    @Operation(summary = "Télécharger le document d'un mémoire")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadThesisDocument(@PathVariable Long id) {

        FileDTO fileDTO = thesisService.downloadDocument(id);

        ByteArrayResource resource = new ByteArrayResource(fileDTO.getBytes());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDTO.getContentType()))
                .contentLength(fileDTO.getBytes().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileDTO.getFileName() + "\"")
                .body(resource);
    }

    // ------------------------------------------------------------
    // Supprimer un mémoire (action réservée à l’administrateur)
    // ------------------------------------------------------------
    @Operation(summary = "Supprimer un mémoire")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThesis(@PathVariable Long id) {
        thesisService.deleteThesis(id);
        return noContent(); // méthode utilitaire héritée de BaseController
    }
}
