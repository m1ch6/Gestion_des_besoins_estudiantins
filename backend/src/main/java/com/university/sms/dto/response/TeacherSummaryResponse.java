package com.university.sms.dto.response;

import lombok.*;

/**
 * DTO « summary » pour un enseignant :
 * – utilisé dans les listes et les relations imbriquées
 * (par ex. dans ThesisResponse, MiniProjectResponse…).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeacherSummaryResponse {

    private Long id;
    private String fullName; // « Prénom Nom »
    private String grade; // Maître-assistant, Professeur, …
    private String department; // Nom du département
}
