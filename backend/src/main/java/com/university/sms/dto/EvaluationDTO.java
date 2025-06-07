package com.university.sms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Note + commentaire lors de l’évaluation d’un miniprojet.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {

    @NotNull
    @DecimalMin("0")
    @DecimalMax("20")
    private Double grade;

    @Size(max = 2000)
    private String feedback;
}
