package com.university.sms.dto.response;

import com.university.sms.entity.ProjectStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO retourné par l’API après toute opération sur un miniprojet.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniprojectResponseDTO {

    private Long id;
    private String title;
    private String description;

    private ProjectStatus status;
    private Double grade;
    private String feedback;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* relations « light » */
    private StudentSummaryResponse student;
    private TeacherSummaryResponse supervisor;

    private List<DocumentResponse> documents;
}
