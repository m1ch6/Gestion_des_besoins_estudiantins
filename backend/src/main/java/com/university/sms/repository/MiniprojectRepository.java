package com.university.sms.repository;

import com.university.sms.entity.Miniproject;
import com.university.sms.entity.ProjectStatus;
import com.university.sms.entity.Student;
import com.university.sms.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des miniprojets
 */
@Repository
public interface MiniprojectRepository extends BaseRepository<Miniproject> {

    /**
     * Trouve tous les projets d'un étudiant
     * 
     * @param student étudiant
     * @return liste des projets
     */
    List<Miniproject> findByStudent(Student student);

    /**
     * Trouve tous les projets d'un étudiant par statut
     * 
     * @param student étudiant
     * @param status  statut recherché
     * @return liste des projets
     */
    List<Miniproject> findByStudentAndStatus(Student student, ProjectStatus status);

    /**
     * Trouve tous les projets supervisés par un enseignant
     * 
     * @param supervisor enseignant superviseur
     * @param pageable   pagination
     * @return page de projets
     */
    Page<Miniproject> findBySupervisor(Teacher supervisor, Pageable pageable);

    /**
     * Trouve tous les projets par statut
     * 
     * @param status   statut recherché
     * @param pageable pagination
     * @return page de projets
     */
    Page<Miniproject> findByStatus(ProjectStatus status, Pageable pageable);

    /**
     * Trouve les projets en attente de validation
     * 
     * @param pageable pagination
     * @return page de projets
     */
    @Query("SELECT m FROM Miniproject m WHERE m.status = 'SUBMITTED' ORDER BY m.createdAt ASC")
    Page<Miniproject> findPendingValidation(Pageable pageable);

    /**
     * Compte les projets par statut pour un étudiant
     * 
     * @param student étudiant
     * @param status  statut
     * @return nombre de projets
     */
    long countByStudentAndStatus(Student student, ProjectStatus status);

    /**
     * Statistiques des projets par statut
     * 
     * @return liste de tuples (statut, count)
     */
    @Query("SELECT m.status, COUNT(m) FROM Miniproject m GROUP BY m.status")
    List<Object[]> getProjectStatisticsByStatus();

    /**
     * Trouve les projets créés dans une période
     * 
     * @param startDate date de début
     * @param endDate   date de fin
     * @return liste des projets
     */
    List<Miniproject> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Vérifie si un titre existe déjà pour un étudiant
     * 
     * @param title   titre à vérifier
     * @param student étudiant
     * @return true si le titre existe
     */
    boolean existsByTitleAndStudent(String title, Student student);
}
