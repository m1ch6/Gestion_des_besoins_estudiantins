package com.university.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité Student - hérite de User
 * Représente un étudiant avec ses projets et mémoires
 */
@Entity
@Table(name = "students")
@DiscriminatorValue("STUDENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Student extends User {

    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String promotion;

    @Column(nullable = false)
    private String speciality;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Miniproject> miniprojects = new ArrayList<>();

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private Thesis thesis;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Teacher supervisor;

    /**
     * Vérifie si l'étudiant peut soumettre un mémoire
     * 
     * @return true si au moins un miniprojet est évalué
     */
    public boolean canSubmitThesis() {
        return miniprojects.stream()
                .anyMatch(mp -> mp.getStatus() == ProjectStatus.EVALUATED);
    }

    /**
     * Calcule la moyenne des notes des miniprojets
     * 
     * @return moyenne des notes ou 0 si aucun projet évalué
     */
    public double getAverageProjectGrade() {
        return miniprojects.stream()
                .filter(mp -> mp.getGrade() != null)
                .mapToDouble(Miniproject::getGrade)
                .average()
                .orElse(0.0);
    }
}
