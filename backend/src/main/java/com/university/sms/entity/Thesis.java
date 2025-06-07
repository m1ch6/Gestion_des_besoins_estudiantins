package com.university.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entité Thesis - représente un mémoire de fin d'études
 */
@Entity
@Table(name = "theses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Thesis extends BaseEntity {

    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @ElementCollection
    @CollectionTable(name = "thesis_keywords", joinColumns = @JoinColumn(name = "thesis_id"))
    @Column(name = "keyword")
    private Set<String> keywords = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Teacher supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThesisStatus status = ThesisStatus.DRAFT;

    @Column(nullable = false)
    private Long version = (long) 1;

    @Override
    public Long getVersion() {
        return version;
    }

    @OneToMany(mappedBy = "thesis", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("uploadDate DESC")
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToOne(mappedBy = "thesis", cascade = CascadeType.ALL)
    private Defense defense;

    private LocalDateTime submissionDate;

    private LocalDateTime validationDate;

    private Double finalGrade;

    /**
     * Soumet le mémoire
     */
    public void submit() {
        if (this.status != ThesisStatus.DRAFT) {
            throw new IllegalStateException("Le mémoire ne peut être soumis que depuis l'état DRAFT");
        }
        this.status = ThesisStatus.SUBMITTED;
        this.submissionDate = LocalDateTime.now();
    }

    /**
     * Met à jour la version du mémoire
     * 
     * @param newDocument nouveau document
     */
    public void updateVersion(Document newDocument) {
        if (this.status != ThesisStatus.SUBMITTED && this.status != ThesisStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Le mémoire ne peut être mis à jour dans cet état");
        }
        this.version++;
        this.documents.add(newDocument);
        newDocument.setThesis(this);
    }

    /**
     * Valide le mémoire
     */
    public void validate() {
        if (this.status != ThesisStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Seul un mémoire en révision peut être validé");
        }
        this.status = ThesisStatus.VALIDATED;
        this.validationDate = LocalDateTime.now();
    }

    /**
     * Programme la soutenance
     * 
     * @param defense information de soutenance
     */
    public void scheduleDefense(Defense defense) {
        if (this.status != ThesisStatus.VALIDATED) {
            throw new IllegalStateException("La soutenance ne peut être programmée que pour un mémoire validé");
        }
        this.defense = defense;
        defense.setThesis(this);
        this.status = ThesisStatus.DEFENSE_SCHEDULED;
    }

    /**
     * Enregistre le résultat de la soutenance
     * 
     * @param grade note finale
     */
    public void recordDefenseResult(Double grade) {
        if (this.status != ThesisStatus.DEFENSE_SCHEDULED) {
            throw new IllegalStateException("Aucune soutenance programmée");
        }
        this.finalGrade = grade;
        this.status = ThesisStatus.DEFENDED;
    }
}
