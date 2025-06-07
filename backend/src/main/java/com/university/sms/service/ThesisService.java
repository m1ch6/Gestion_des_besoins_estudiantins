package com.university.sms.service;

import com.university.sms.dto.ThesisCreateDTO;
import com.university.sms.dto.response.ThesisResponseDTO;
import com.university.sms.dto.DefenseScheduleDTO;
import com.university.sms.dto.FileDTO;
import com.university.sms.entity.*;
import com.university.sms.exception.BusinessException;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.service.mapper.ThesisMapper;
import com.university.sms.repository.StudentRepository;
import com.university.sms.repository.TeacherRepository;
import com.university.sms.repository.ThesisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des mémoires
 */
@Service
@Transactional
@Slf4j
public class ThesisService extends BaseService<Thesis, ThesisRepository> {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private final ThesisMapper mapper;

    public ThesisService(ThesisRepository repository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            DocumentService documentService,
            NotificationService notificationService,
            ThesisMapper mapper) {
        super(repository);
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.documentService = documentService;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Override
    protected String getEntityName() {
        return "Thesis";
    }

    /**
     * Crée un nouveau mémoire
     * 
     * @param dto       données du mémoire
     * @param studentId ID de l'étudiant
     * @param file      fichier PDF du mémoire
     * @return mémoire créé
     */
    public ThesisResponseDTO createThesis(ThesisCreateDTO dto, Long studentId, MultipartFile file) {
        log.info("Création d'un nouveau mémoire pour l'étudiant ID: {}", studentId);

        // Vérifier que l'étudiant existe et peut soumettre un mémoire
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        // Vérifier l'éligibilité
        if (!student.canSubmitThesis()) {
            throw new BusinessException("Vous devez d'abord terminer et faire évaluer au moins un miniprojet");
        }

        // Vérifier qu'il n'a pas déjà un mémoire
        if (repository.findByStudent(student).isPresent()) {
            throw new BusinessException("Vous avez déjà soumis un mémoire");
        }

        // Vérifier l'unicité du titre
        if (repository.existsByTitle(dto.getTitle())) {
            throw new BusinessException("Ce titre est déjà utilisé par un autre mémoire");
        }

        // Vérifier que l'étudiant a un superviseur
        if (student.getSupervisor() == null) {
            throw new BusinessException("Aucun superviseur n'est assigné à votre compte");
        }

        // Créer le mémoire
        Thesis thesis = Thesis.builder()
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .keywords(Set.copyOf(dto.getKeywords()))
                .student(student)
                .supervisor(student.getSupervisor())
                .status(ThesisStatus.DRAFT)
                .version((long) 1)
                .build();

        thesis = repository.save(thesis);

        // Sauvegarder le document
        if (file != null && !file.isEmpty()) {
            Document document = documentService.uploadThesisDocument(file, thesis);
            thesis.getDocuments().add(document);
        }

        log.info("Mémoire créé avec succès. ID: {}", thesis.getId());
        return mapper.toResponse(thesis);
    }

    /**
     * Soumet un mémoire pour validation
     * 
     * @param thesisId  ID du mémoire
     * @param studentId ID de l'étudiant
     * @return mémoire soumis
     */
    public ThesisResponseDTO submitThesis(Long thesisId, Long studentId) {
        log.info("Soumission du mémoire ID: {} par l'étudiant ID: {}", thesisId, studentId);

        Thesis thesis = findById(thesisId);

        // Vérifications
        if (!thesis.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à soumettre ce mémoire");
        }

        if (thesis.getDocuments().isEmpty()) {
            throw new BusinessException("Au moins un document doit être attaché au mémoire");
        }

        // Soumettre
        thesis.submit();
        thesis = repository.save(thesis);

        // Notifier le superviseur
        notificationService.notifySupervisorThesisSubmitted(thesis);

        log.info("Mémoire soumis avec succès");
        return mapper.toResponse(thesis);
    }

    /**
     * Met à jour la version d'un mémoire
     * 
     * @param thesisId  ID du mémoire
     * @param studentId ID de l'étudiant
     * @param file      nouveau fichier
     * @return mémoire mis à jour
     */
    public ThesisResponseDTO updateThesisVersion(Long thesisId, Long studentId, MultipartFile file) {
        log.info("Mise à jour de la version du mémoire ID: {}", thesisId);

        Thesis thesis = findById(thesisId);

        // Vérifications
        if (!thesis.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à modifier ce mémoire");
        }

        // Créer une nouvelle version du document
        Document newDocument = documentService.uploadThesisDocument(file, thesis);
        thesis.updateVersion(newDocument);

        thesis = repository.save(thesis);

        // Notifier le superviseur
        notificationService.notifySupervisorThesisUpdated(thesis);

        log.info("Version du mémoire mise à jour. Nouvelle version: {}", thesis.getVersion());
        return mapper.toResponse(thesis);
    }

    /**
     * Met le mémoire en révision
     * 
     * @param thesisId  ID du mémoire
     * @param teacherId ID de l'enseignant
     * @return mémoire en révision
     */
    public ThesisResponseDTO putThesisUnderReview(Long thesisId, Long teacherId) {
        log.info("Mise en révision du mémoire ID: {} par l'enseignant ID: {}", thesisId, teacherId);

        Thesis thesis = findById(thesisId);

        // Vérifier que l'enseignant est le superviseur
        if (!thesis.getSupervisor().getId().equals(teacherId)) {
            throw new BusinessException("Seul le superviseur peut mettre ce mémoire en révision");
        }

        if (thesis.getStatus() != ThesisStatus.SUBMITTED) {
            throw new BusinessException("Le mémoire doit être soumis pour être mis en révision");
        }

        thesis.setStatus(ThesisStatus.UNDER_REVIEW);
        thesis = repository.save(thesis);

        log.info("Mémoire mis en révision");
        return mapper.toResponse(thesis);
    }

    /**
     * Valide un mémoire
     * 
     * @param thesisId  ID du mémoire
     * @param teacherId ID de l'enseignant
     * @return mémoire validé
     */
    public ThesisResponseDTO validateThesis(Long thesisId, Long teacherId) {
        log.info("Validation du mémoire ID: {} par l'enseignant ID: {}", thesisId, teacherId);

        Thesis thesis = findById(thesisId);

        // Vérifier que l'enseignant est le superviseur
        if (!thesis.getSupervisor().getId().equals(teacherId)) {
            throw new BusinessException("Seul le superviseur peut valider ce mémoire");
        }

        thesis.validate();
        thesis = repository.save(thesis);

        // Notifier l'étudiant et l'administration
        notificationService.notifyStudentThesisValidated(thesis);
        notificationService.notifyAdminThesisReadyForDefense(thesis);

        log.info("Mémoire validé avec succès");
        return mapper.toResponse(thesis);
    }

    /**
     * Rejette un mémoire
     * 
     * @param thesisId  ID du mémoire
     * @param teacherId ID de l'enseignant
     * @param feedback  raison du rejet
     * @return mémoire rejeté
     */
    public ThesisResponseDTO rejectThesis(Long thesisId, Long teacherId, String feedback) {
        log.info("Rejet du mémoire ID: {} par l'enseignant ID: {}", thesisId, teacherId);

        Thesis thesis = findById(thesisId);

        // Vérifier que l'enseignant est le superviseur
        if (!thesis.getSupervisor().getId().equals(teacherId)) {
            throw new BusinessException("Seul le superviseur peut rejeter ce mémoire");
        }

        if (thesis.getStatus() != ThesisStatus.UNDER_REVIEW) {
            throw new BusinessException("Le mémoire doit être en révision pour être rejeté");
        }

        thesis.setStatus(ThesisStatus.REJECTED);
        thesis = repository.save(thesis);

        // Notifier l'étudiant avec le feedback
        notificationService.notifyStudentThesisRejected(thesis, feedback);

        log.info("Mémoire rejeté");
        return mapper.toResponse(thesis);
    }

    /**
     * Programme une soutenance
     * 
     * @param thesisId    ID du mémoire
     * @param scheduleDTO données de la soutenance
     * @return mémoire avec soutenance programmée
     */
    public ThesisResponseDTO scheduleDefense(Long thesisId, DefenseScheduleDTO scheduleDTO) {
        log.info("Programmation de la soutenance pour le mémoire ID: {}", thesisId);

        Thesis thesis = findById(thesisId);

        // Vérifier la date
        if (scheduleDTO.getDefenseDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("La date de soutenance ne peut pas être dans le passé");
        }

        // Vérifier les membres du jury
        List<Teacher> jury = scheduleDTO.getJuryIds().stream()
                .map(id -> teacherRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Membre du jury non trouvé: " + id)))
                .collect(Collectors.toList());

        if (jury.size() < 2) {
            throw new BusinessException("Le jury doit comporter au moins 2 membres");
        }

        // Créer la soutenance
        Defense defense = Defense.builder()
                .defenseDate(scheduleDTO.getDefenseDate())
                .location(scheduleDTO.getLocation())
                .jury(jury)
                .build();

        thesis.scheduleDefense(defense);
        thesis = repository.save(thesis);

        // Notifier toutes les parties concernées
        notificationService.notifyDefenseScheduled(thesis);

        log.info("Soutenance programmée pour le {}", scheduleDTO.getDefenseDate());
        return mapper.toResponse(thesis);
    }

    /**
     * Enregistre le résultat d'une soutenance
     * 
     * @param thesisId ID du mémoire
     * @param grade    note finale
     * @param feedback observations
     * @return mémoire soutenu
     */
    public ThesisResponseDTO recordDefenseResult(Long thesisId, Double grade, String feedback) {
        log.info("Enregistrement du résultat de soutenance pour le mémoire ID: {}", thesisId);

        Thesis thesis = findById(thesisId);

        // Valider la note
        if (grade < 0 || grade > 20) {
            throw new BusinessException("La note doit être comprise entre 0 et 20");
        }

        thesis.recordDefenseResult(grade);
        thesis.getDefense().setGrade(grade);
        thesis.getDefense().setObservations(feedback);

        thesis = repository.save(thesis);

        // Notifier l'étudiant
        notificationService.notifyStudentDefenseResult(thesis);

        log.info("Résultat de soutenance enregistré. Note: {}", grade);
        return mapper.toResponse(thesis);
    }

    /**
     * Trouve le mémoire d'un étudiant
     * 
     * @param studentId ID de l'étudiant
     * @return mémoire si trouvé
     */
    public ThesisResponseDTO findByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        return repository.findByStudent(student)
                .map(mapper::toResponse)
                .orElse(null);
    }

    /**
     * Trouve les mémoires supervisés par un enseignant
     * 
     * @param teacherId ID de l'enseignant
     * @param pageable  pagination
     * @return page de mémoires
     */
    public Page<ThesisResponseDTO> findBySupervisor(Long teacherId, Pageable pageable) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));

        return repository.findBySupervisor(teacher, pageable)
                .map(mapper::toResponse);
    }

    /**
     * Recherche de mémoires par mots-clés
     * 
     * @param keyword mot-clé
     * @return liste des mémoires
     */
    public List<ThesisResponseDTO> searchByKeyword(String keyword) {
        return repository.findByKeyword(keyword).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Trouve les mémoires en attente de validation
     * 
     * @return liste des mémoires
     */
    public List<ThesisResponseDTO> findPendingValidation() {
        return repository.findPendingValidation().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /* Télécharger le dernier document soumis */
    public FileDTO downloadDocument(Long thesisId) {

        Thesis thesis = findById(thesisId);

        if (thesis.getDocuments().isEmpty())
            throw new BusinessException("Aucun document attaché");

        Document latest = thesis.getDocuments().get(0); // grâce au @OrderBy(uploadDate DESC)
        return documentService.downloadDocument(latest);
    }

    /* Supprimer un mémoire (appelé par DELETE /theses/{id}) */
    public void deleteThesis(Long thesisId) {
        Thesis thesis = findById(thesisId);
        // supprimer physiquement tous les fichiers
        thesis.getDocuments().forEach(documentService::deleteDocument);
        repository.delete(thesis);
    }

}
