package com.university.sms.service;

import com.university.sms.dto.FileDTO;
import com.university.sms.entity.*;
import com.university.sms.exception.BusinessException;
import com.university.sms.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service chargé :
 * – de valider et stocker physiquement les fichiers,
 * – de créer / supprimer l’entité Document en BDD,
 * – de fournir un FileDTO pour le download.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /* ------------------------------------------------------------------ */
    /* Méthodes « upload » */
    /* ------------------------------------------------------------------ */

    public Document uploadDocument(MultipartFile file, Miniproject project) {
        return internalUpload(file, DocumentType.MINIPROJECT, project, null);
    }

    public Document uploadThesisDocument(MultipartFile file, Thesis thesis) {
        return internalUpload(file, DocumentType.THESIS, null, thesis);
    }

    private Document internalUpload(MultipartFile file,
            DocumentType type,
            Miniproject project,
            Thesis thesis) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Aucun fichier reçu");
        }
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException ex) {
            throw new BusinessException("Impossible de préparer le répertoire d’upload", ex);
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + originalName;
        Path target = Paths.get(uploadDir).resolve(storedName).normalize();

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException("Erreur lors de la sauvegarde du fichier", ex);
        }

        Document document = Document.builder()
                .fileName(originalName)
                .contentType(file.getContentType())
                .size(file.getSize())
                .storagePath(target.toString())
                .type(type)
                .uploadDate(LocalDateTime.now())
                .miniproject(project)
                .thesis(thesis)
                .build();

        return documentRepository.save(document);
    }

    /* ------------------------------------------------------------------ */
    /* Lecture / suppression */
    /* ------------------------------------------------------------------ */

    public FileDTO downloadDocument(Document document) {

        Path path = Paths.get(document.getStoragePath());
        if (!Files.exists(path)) {
            throw new BusinessException("Le fichier n’existe pas sur le serveur");
        }

        try {
            return FileDTO.builder()
                    .fileName(document.getFileName())
                    .contentType(document.getContentType())
                    .bytes(Files.readAllBytes(path))
                    .build();
        } catch (IOException ex) {
            throw new BusinessException("Erreur lors de la lecture du fichier", ex);
        }
    }

    public void deleteDocument(Document document) {

        Path path = Paths.get(document.getStoragePath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            log.warn("Fichier non supprimé : {}", path);
        }

        documentRepository.delete(document);
    }
}
