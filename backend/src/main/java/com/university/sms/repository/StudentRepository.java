package com.university.sms.repository;

import com.university.sms.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends BaseRepository<Student> {

    /* === Accès par matricule === */
    Optional<Student> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    /* === Filtres classiques (facultatifs) === */
    List<Student> findByPromotion(String promotion);

    List<Student> findBySpeciality(String speciality);
}
