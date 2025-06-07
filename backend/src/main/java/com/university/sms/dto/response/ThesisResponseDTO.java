package com.university.sms.dto.response;

import com.university.sms.entity.ThesisStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO complet retourné par l’API pour un mémoire.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThesisResponseDTO {

    private Long id;
    private String title;
    private String summary;
    private Set<String> keywords;

    private ThesisStatus status;
    private String statusDisplay; // rempli par le mapper
    private Integer version;

    private LocalDateTime submissionDate;
    private LocalDateTime validationDate;
    private Double finalGrade;

    private StudentSummaryResponse student;
    private TeacherSummaryResponse supervisor;
    private DefenseResponse defense;

    private List<DocumentResponse> documents;
}
