package com.university.sms.service;

import com.university.sms.entity.*;
import com.university.sms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;
    @Value("${notification.email.from:noreply@university.com}")
    private String mailFrom;

    /*
     * ============================================================
     * Méthodes publiques
     * ============================================================
     */

    public void notifySupervisorThesisSubmitted(Thesis t) {
        create(t.getSupervisor(), NotificationType.THESIS,
                "Nouveau mémoire soumis",
                "Le mémoire « " + t.getTitle() + " » vient d’être soumis.");
    }

    public void notifySupervisorThesisUpdated(Thesis t) {
        create(t.getSupervisor(), NotificationType.THESIS,
                "Nouvelle version du mémoire",
                "Version " + t.getVersion() + " du mémoire « " + t.getTitle() + " ».");
    }

    public void notifyStudentThesisValidated(Thesis t) {
        create(t.getStudent(), NotificationType.THESIS,
                "Mémoire validé", "Votre mémoire a été validé.");
    }

    public void notifyAdminThesisReadyForDefense(Thesis t) {
        // Exemple : récupérer tous les admins via UserRepository
    }

    public void notifyStudentThesisRejected(Thesis t, String fb) {
        create(t.getStudent(), NotificationType.THESIS,
                "Mémoire rejeté", "Motif : " + fb);
    }

    public void notifyDefenseScheduled(Thesis t) {
        String msg = "Soutenance prévue le " + t.getDefense().getDefenseDate()
                + " à " + t.getDefense().getLocation();
        create(t.getStudent(), NotificationType.DEFENSE, "Soutenance programmée", msg);
        t.getDefense().getJury()
                .forEach(j -> create(j, NotificationType.DEFENSE, "Convocation jury", msg));
    }

    public void notifyStudentDefenseResult(Thesis t) {
        create(t.getStudent(), NotificationType.DEFENSE,
                "Résultat soutenance",
                "Note finale : " + t.getFinalGrade());
    }

    public void notifyTeachersNewProject(Miniproject p) {
        // Exemple : informer tous les enseignants du département
    }

    public void notifyStudentProjectValidated(Miniproject p) {
        create(p.getStudent(), NotificationType.MINIPROJECT,
                "Projet validé", "Votre projet a été validé.");
    }

    public void notifyStudentProjectRejected(Miniproject p) {
        create(p.getStudent(), NotificationType.MINIPROJECT,
                "Projet rejeté", "Motif : " + p.getFeedback());
    }

    public void notifyStudentProjectEvaluated(Miniproject p) {
        create(p.getStudent(), NotificationType.MINIPROJECT,
                "Note projet", "Vous avez obtenu " + p.getGrade() + "/20.");
    }

    /*
     * ============================================================
     * Méthodes utilitaires
     * ============================================================
     */

    private void create(User user, NotificationType type, String title, String msg) {

        Notification n = Notification.builder()
                .recipient(user)
                .type(type)
                .title(title)
                .message(msg)
                .build();
        notificationRepository.save(n);

        if (emailEnabled)
            sendMail(user.getEmail(), title, msg);
    }

    private void sendMail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(mailFrom);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
        } catch (Exception ex) {
            log.warn("Échec d’envoi d’e-mail à {}", to);
        }
    }
}
