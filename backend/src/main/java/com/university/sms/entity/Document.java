package com.university.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité Document – représente un fichier (PDF, ZIP, …)
 * associé à un miniprojet ou à un mémoire.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {

    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size; // en octets

    @Column(nullable = false)
    private String storagePath; // chemin ou identifiant de stockage

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @Column(nullable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    /* ---------- Relations ---------- */

    /** Projet porteur (nullable : document de mémoire uniquement). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miniproject_id")
    private Miniproject miniproject;

    /** Mémoire porteur (nullable : document de miniprojet uniquement). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thesis_id")
    private Thesis thesis;
}
