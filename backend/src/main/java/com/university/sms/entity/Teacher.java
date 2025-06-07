package com.university.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité Teacher – hérite de User.
 * Un enseignant peut superviser des miniprojets et des mémoires
 * et être membre d’un jury de soutenance.
 */
@Entity
@Table(name = "teachers")
@DiscriminatorValue("TEACHER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {

    private Long id;

    @Column(nullable = false)
    private String fullName;

    /** Grade universitaire : Maître-assistant, Professeur… */
    @Column(nullable = false)
    private String grade;

    /** Département ou filière d’appartenance. */
    @Column(nullable = false)
    private String department;

    /* ---------- Relations ---------- */

    /** Miniprojets dont ce teacher est le superviseur. */
    @OneToMany(mappedBy = "supervisor")
    private List<Miniproject> supervisedProjects = new ArrayList<>();

    /** Mémoires dont ce teacher est le superviseur. */
    @OneToMany(mappedBy = "supervisor")
    private List<Thesis> supervisedTheses = new ArrayList<>();

    /** Jurys de soutenance auxquels il participe. */
    @ManyToMany(mappedBy = "jury")
    private List<Defense> defenses = new ArrayList<>();
}
