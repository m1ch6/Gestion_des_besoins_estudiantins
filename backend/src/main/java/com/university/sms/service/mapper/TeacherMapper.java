package com.university.sms.service.mapper;

import com.university.sms.dto.response.TeacherSummaryResponse;
import com.university.sms.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherMapper {

    @Mapping(target = "fullName", expression = "java(t.getFirstName() + \" \" + t.getLastName())")
    TeacherSummaryResponse toSummary(Teacher t);

    List<TeacherSummaryResponse> toSummaryList(List<Teacher> teachers);
}
