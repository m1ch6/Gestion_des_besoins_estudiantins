package com.university.sms.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Informations minimales sur un document stocké.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private Long size;
    private LocalDateTime uploadDate;
}
