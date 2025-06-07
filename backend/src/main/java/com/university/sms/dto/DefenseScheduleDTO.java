package com.university.sms.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Informations transmises par l’admin pour programmer la soutenance.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseScheduleDTO {

    @NotNull
    private LocalDateTime defenseDate;

    @NotBlank
    private String location;

    @NotEmpty
    private List<Long> juryIds; // ids des enseignants
}
