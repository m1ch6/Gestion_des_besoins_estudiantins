package com.university.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Defense – informations de soutenance d’un mémoire.
 */
@Entity
@Table(name = "defenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Defense extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime defenseDate;

    @Column(nullable = false)
    private String location;

    /* ---------- Relations ---------- */

    /** Mémoire concerné (relation 1-1). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thesis_id", nullable = false, unique = true)
    private Thesis thesis;

    /** Membres du jury (plusieurs enseignants). */
    @ManyToMany
    @JoinTable(name = "defense_jury", joinColumns = @JoinColumn(name = "defense_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> jury = new ArrayList<>();

    /* ---------- Résultat ---------- */
    private Double grade;
    @Column(columnDefinition = "TEXT")
    private String observations;
}
