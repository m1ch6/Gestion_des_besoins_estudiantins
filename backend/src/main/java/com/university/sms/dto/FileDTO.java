package com.university.sms.dto;

import lombok.*;

/**
 * Petit DTO pour transporter un fichier (download).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

    private String fileName;
    private String contentType;
    private byte[] bytes;
}
