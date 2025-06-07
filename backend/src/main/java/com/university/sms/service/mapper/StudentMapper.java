package com.university.sms.service.mapper;

import com.university.sms.dto.response.StudentSummaryResponse;
import com.university.sms.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper MapStruct pour convertir l’entité {@link Student}
 * vers un DTO léger (summary) destiné aux listes et références.
 *
 * Si demain vous avez besoin d’un DTO détaillé (StudentResponse),
 * il suffira d’ajouter une méthode de mapping supplémentaire.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    /* === Mapping élémentaire === */
    @Mapping(target = "fullName", source = "student.fullName")
    StudentSummaryResponse toSummary(Student student);

    /* === Mapping collection === */
    List<StudentSummaryResponse> toSummaryList(List<Student> students);
}
