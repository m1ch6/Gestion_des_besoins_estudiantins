package com.university.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité Miniproject - représente un mini-projet étudiant
 */
@Entity
@Table(name = "miniprojects")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Miniproject extends BaseEntity {

    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Teacher supervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.DRAFT;

    private Double grade;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @OneToMany(mappedBy = "miniproject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    /**
     * Soumet le projet pour validation
     */
    public void submit() {
        if (this.status != ProjectStatus.DRAFT) {
            throw new IllegalStateException("Le projet ne peut être soumis que depuis l'état DRAFT");
        }
        this.status = ProjectStatus.SUBMITTED;
    }

    /**
     * Valide le projet
     * 
     * @param supervisor l'enseignant qui valide
     */
    public void validate(Teacher supervisor) {
        if (this.status != ProjectStatus.SUBMITTED) {
            throw new IllegalStateException("Seul un projet soumis peut être validé");
        }
        this.supervisor = supervisor;
        this.status = ProjectStatus.VALIDATED;
    }

    /**
     * Rejette le projet avec feedback
     * 
     * @param feedback raison du rejet
     */
    public void reject(String feedback) {
        if (this.status != ProjectStatus.SUBMITTED) {
            throw new IllegalStateException("Seul un projet soumis peut être rejeté");
        }
        this.feedback = feedback;
        this.status = ProjectStatus.REJECTED;
    }

    /**
     * Évalue le projet
     * 
     * @param grade    note attribuée
     * @param feedback commentaires
     */
    public void evaluate(Double grade, String feedback) {
        if (this.status != ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Seul un projet terminé peut être évalué");
        }
        this.grade = grade;
        this.feedback = feedback;
        this.status = ProjectStatus.EVALUATED;
    }
}
