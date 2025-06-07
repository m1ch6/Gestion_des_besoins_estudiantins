package com.university.sms.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Service d’envoi d’e-mails très simplifié : une méthode send()
 * utilisée (par exemple) dans NotificationService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MailUtil {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from:noreply@university.com}")
    private String from;

    /**
     * Envoie un e-mail texte.
     * Renvoie true si l’envoi s’est déroulé sans exception afin
     * que l’appelant puisse, le cas échéant, logguer un fallback.
     */
    public boolean send(String to, String subject, String body) {

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
            return true;
        } catch (MailException ex) {
            log.warn("Échec d’envoi d’e-mail à {} ({})", to, ex.getMessage());
            return false;
        }
    }
}
