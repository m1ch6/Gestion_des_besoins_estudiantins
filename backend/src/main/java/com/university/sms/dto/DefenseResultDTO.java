package com.university.sms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Note finale + observations de la soutenance.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseResultDTO {

    @NotNull
    @DecimalMin("0")
    @DecimalMax("20")
    private Double grade;

    @Size(max = 4000)
    private String feedback;
}
