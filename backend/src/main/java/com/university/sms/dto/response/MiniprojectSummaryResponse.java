package com.university.sms.dto.response;

import com.university.sms.entity.ProjectStatus;
import lombok.*;

/**
 * Résumé léger d’un miniprojet.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniprojectSummaryResponse {

    private Long id;
    private String title;
    private ProjectStatus status;
    private Double grade;
    private String studentName;
}
