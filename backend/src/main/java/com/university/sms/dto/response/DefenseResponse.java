package com.university.sms.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Détails de la soutenance (renvoyé dans ThesisResponseDTO).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseResponse {

    private Long id;
    private LocalDateTime defenseDate;
    private String location;
    private List<TeacherSummaryResponse> jury;
    private Double grade;
    private String observations;
}
