package com.university.sms.dto.response;

import com.university.sms.entity.ThesisStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Résumé léger (liste / tableau).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThesisSummaryResponse {

    private Long id;
    private String title;
    private ThesisStatus status;
    private LocalDateTime validationDate;
    private String studentName;
    private String supervisorName;
}
