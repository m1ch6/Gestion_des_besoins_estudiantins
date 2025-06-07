package com.university.sms.dto.response;

import lombok.*;

/**
 * DTO “léger” représentant un étudiant :
 * uniquement les champs souvent nécessaires
 * dans les listes ou comme propriété imbriquée.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentSummaryResponse {

    private Long id;
    private String matricule;
    private String fullName; // « Prénom Nom »
    private String promotion;
    private String speciality;
}
