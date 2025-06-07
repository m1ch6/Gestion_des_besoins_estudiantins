package com.university.sms.service.mapper;

import com.university.sms.dto.response.*;
import com.university.sms.entity.Thesis;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper qui convertit l’entité Thesis en DTO
 * (et inversement si besoin).
 *
 * Les autres entités référencées (Student, Teacher, Document, Defense)
 * seront traitées par leurs propres mappers — à générer par la suite.
 */
@Mapper(componentModel = "spring", uses = { StudentMapper.class,
        TeacherMapper.class,
        DocumentMapper.class,
        DefenseMapper.class })
public interface ThesisMapper {

    /* === Mapping détaillé === */
    // Removed invalid mapping for status.displayName; adjust as needed if a valid
    // property exists
    ThesisResponseDTO toResponse(Thesis thesis);

    /* === Mapping « summary » pour les listes === */
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "supervisorName", source = "supervisor.fullName")
    ThesisSummaryResponse toSummary(Thesis thesis);

    /* === Collections === */
    List<ThesisSummaryResponse> toSummaryList(List<Thesis> theses);

    /* === Post-traitement pour éviter les listes nulles === */
    @AfterMapping
    default void initLists(@MappingTarget ThesisResponseDTO dto) {
        if (dto.getDocuments() == null)
            dto.setDocuments(List.of());
    }
}
