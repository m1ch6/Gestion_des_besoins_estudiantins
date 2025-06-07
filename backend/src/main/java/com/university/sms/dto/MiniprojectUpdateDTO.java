package com.university.sms.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Données facultatives pour la mise à jour d’un miniprojet
 * (titre / description). Les deux champs sont optionnels.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniprojectUpdateDTO {

    @Size(min = 1, max = 255)
    private String title;

    @Size(max = 5000)
    private String description;
}
