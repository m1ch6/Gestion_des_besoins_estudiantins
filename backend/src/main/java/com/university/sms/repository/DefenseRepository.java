package com.university.sms.repository;

import com.university.sms.entity.Defense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DefenseRepository extends BaseRepository<Defense> {

    /* Trouver les soutenances dans une plage de dates (planning) */
    @Query("SELECT d FROM Defense d WHERE d.defenseDate BETWEEN :start AND :end ORDER BY d.defenseDate")
    List<Defense> findBetweenDates(@Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
