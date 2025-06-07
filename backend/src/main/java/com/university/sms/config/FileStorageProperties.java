package com.university.sms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lit la section :
 *
 * file:
 * upload-dir: ./uploads
 * max-size: 10MB
 * allowed-types: pdf,doc,docx,zip
 *
 * du fichier application.yml afin de centraliser ces valeurs.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    /** Répertoire physique où les fichiers seront stockés. */
    private String uploadDir = "./uploads";

    /** Taille max autorisée (ex. "10MB"). Utilisée côté contrôleur/validator. */
    private String maxSize = "10MB";

    /** Extensions autorisées pour l’upload (sans le point). */
    private List<String> allowedTypes = List.of("pdf", "doc", "docx", "zip");
}
