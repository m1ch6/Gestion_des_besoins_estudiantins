package com.university.sms.service.mapper;

import com.university.sms.dto.response.DefenseResponse;
import com.university.sms.entity.Defense;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { TeacherMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DefenseMapper {

    DefenseResponse toResponse(Defense entity);
}
