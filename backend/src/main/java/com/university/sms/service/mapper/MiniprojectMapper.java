package com.university.sms.service.mapper;

import com.university.sms.dto.response.MiniprojectResponseDTO;
import com.university.sms.dto.response.MiniprojectSummaryResponse;
import com.university.sms.entity.Miniproject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = { StudentMapper.class, TeacherMapper.class,
        DocumentMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MiniprojectMapper {

    MiniprojectResponseDTO toResponseDTO(Miniproject entity);

    @Mapping(target = "studentName", source = "student.fullName")
    MiniprojectSummaryResponse toSummary(Miniproject entity);

    List<MiniprojectSummaryResponse> toSummaryList(List<Miniproject> entities);
}
