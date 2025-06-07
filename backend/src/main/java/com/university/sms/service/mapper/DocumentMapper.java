package com.university.sms.service.mapper;

import com.university.sms.dto.response.DocumentResponse;
import com.university.sms.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper {

    DocumentResponse toResponse(Document entity);

    List<DocumentResponse> toResponseList(List<Document> entities);
}
