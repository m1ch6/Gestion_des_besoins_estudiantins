package com.university.sms.repository;

import com.university.sms.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThesisRepository extends BaseRepository<Thesis> {

    /* === Existence / unicité === */
    boolean existsByTitle(String title);

    /* === Accès direct === */
    Optional<Thesis> findByStudent(Student student);

    Page<Thesis> findBySupervisor(Teacher supervisor, Pageable pageable);

    /* === Recherche plein-texte (titre, résumé, mots-clés) === */
    @Query("""
            SELECT DISTINCT t
            FROM Thesis t LEFT JOIN t.keywords k
            WHERE LOWER(t.title)   LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(t.summary) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(k)         LIKE LOWER(CONCAT('%', :kw, '%'))
            """)
    List<Thesis> findByKeyword(@Param("kw") String keyword);

    /* === Mémoires en attente de validation (UNDER_REVIEW) === */
    @Query("SELECT t FROM Thesis t WHERE t.status = 'UNDER_REVIEW' ORDER BY t.submissionDate ASC")
    List<Thesis> findPendingValidation();
}
