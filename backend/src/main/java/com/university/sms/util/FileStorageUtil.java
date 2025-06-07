package com.university.sms.util;

import com.university.sms.config.FileStorageProperties;
import com.university.sms.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

/**
 * Centralise toutes les opérations d’E/S liées aux fichiers :
 * – validation (taille, extension)
 * – génération d’un nom unique
 * – sauvegarde physique
 * – lecture / suppression
 *
 * DocumentService (ou tout autre service) peut l’injecter
 * pour éviter de réimplémenter ces opérations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageUtil {

    private final FileStorageProperties props;

    /* ====================================================================== */
    /* Upload */
    /* ====================================================================== */

    /**
     * Stocke le fichier dans le répertoire configuré et renvoie
     * le chemin absolu du fichier créé.
     */
    public String store(MultipartFile file) {

        validate(file);

        Path uploadRoot = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException ex) {
            throw new BusinessException("Impossible de préparer le répertoire d’upload", ex);
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String stored = UUID.randomUUID() + "_" + original;
        Path target = uploadRoot.resolve(stored);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException("Erreur lors de la sauvegarde du fichier", ex);
        }
        return target.toString();
    }

    /* ====================================================================== */
    /* Lecture / suppression / utilitaires */
    /* ====================================================================== */

    public byte[] read(String absolutePath) {
        try {
            Path path = Paths.get(absolutePath);
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new BusinessException("Impossible de lire le fichier : " + absolutePath, ex);
        }
    }

    public void delete(String absolutePath) {
        try {
            FileSystemUtils.deleteRecursively(Paths.get(absolutePath));
        } catch (IOException ex) {
            log.warn("Suppression fichier impossible : {}", absolutePath);
        }
    }

    /* ====================================================================== */
    /* Validation */
    /* ====================================================================== */

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Aucun fichier reçu");
        }

        // Taille
        DataSize max = DataSize.parse(props.getMaxSize());
        if (file.getSize() > max.toBytes()) {
            throw new BusinessException("Le fichier dépasse la taille maximale autorisée (" + props.getMaxSize() + ")");
        }

        // Extension
        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        List<String> allowed = props.getAllowedTypes();
        if (ext == null || allowed.stream().noneMatch(ext::equalsIgnoreCase)) {
            throw new BusinessException("Extension non autorisée : ." + ext);
        }
    }
}
