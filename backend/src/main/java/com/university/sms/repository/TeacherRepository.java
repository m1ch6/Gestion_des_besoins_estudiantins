package com.university.sms.repository;

import com.university.sms.entity.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends BaseRepository<Teacher> {

    List<Teacher> findByDepartment(String department);

    /* Recherche par nom / prénom (utile pour former un jury) */
    @Query("SELECT t FROM Teacher t WHERE " +
            "LOWER(t.firstName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(t.lastName)  LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Teacher> searchByName(@Param("kw") String keyword);
}
