package com.university.sms.service;

import com.university.sms.dto.MiniprojectCreateDTO;
import com.university.sms.dto.MiniprojectUpdateDTO;
import com.university.sms.dto.response.MiniprojectResponseDTO;
import com.university.sms.entity.*;
import com.university.sms.exception.BusinessException;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.service.mapper.MiniprojectMapper;
import com.university.sms.repository.MiniprojectRepository;
import com.university.sms.repository.StudentRepository;
import com.university.sms.repository.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des miniprojets
 */
@Service
@Transactional
@Slf4j
public class MiniprojectService extends BaseService<Miniproject, MiniprojectRepository> {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private final MiniprojectMapper mapper;

    public MiniprojectService(MiniprojectRepository repository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            DocumentService documentService,
            NotificationService notificationService,
            MiniprojectMapper mapper) {
        super(repository);
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.documentService = documentService;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Override
    protected String getEntityName() {
        return "Miniproject";
    }

    /**
     * Crée un nouveau miniprojet
     * 
     * @param dto       données du miniprojet
     * @param studentId ID de l'étudiant
     * @param files     fichiers associés
     * @return miniprojet créé
     */
    public MiniprojectResponseDTO createMiniproject(MiniprojectCreateDTO dto, Long studentId,
            List<MultipartFile> files) {
        log.info("Création d'un nouveau miniprojet pour l'étudiant ID: {}", studentId);

        // Vérifier que l'étudiant existe
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        // Vérifier que le titre n'existe pas déjà pour cet étudiant
        if (repository.existsByTitleAndStudent(dto.getTitle(), student)) {
            throw new BusinessException("Vous avez déjà un projet avec ce titre");
        }

        // Créer le miniprojet
        Miniproject miniproject = Miniproject.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .student(student)
                .status(ProjectStatus.DRAFT)
                .build();

        miniproject = repository.save(miniproject);

        // Gérer les documents
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Document document = documentService.uploadDocument(file, miniproject);
                miniproject.getDocuments().add(document);
            }
        }

        log.info("Miniprojet créé avec succès. ID: {}", miniproject.getId());
        return mapper.toResponseDTO(miniproject);
    }

    /**
     * Soumet un miniprojet pour validation
     * 
     * @param projectId ID du projet
     * @param studentId ID de l'étudiant (pour vérification)
     * @return projet mis à jour
     */
    public MiniprojectResponseDTO submitMiniproject(Long projectId, Long studentId) {
        log.info("Soumission du miniprojet ID: {} par l'étudiant ID: {}", projectId, studentId);

        Miniproject miniproject = findById(projectId);

        // Vérifier que l'étudiant est bien le propriétaire
        if (!miniproject.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à soumettre ce projet");
        }

        // Vérifier qu'au moins un document est attaché
        if (miniproject.getDocuments().isEmpty()) {
            throw new BusinessException("Au moins un document doit être attaché au projet");
        }

        // Soumettre le projet
        miniproject.submit();
        miniproject = repository.save(miniproject);

        // Notifier les enseignants
        notificationService.notifyTeachersNewProject(miniproject);

        log.info("Miniprojet soumis avec succès");
        return mapper.toResponseDTO(miniproject);
    }

    /**
     * Valide un miniprojet
     * 
     * @param projectId ID du projet
     * @param teacherId ID de l'enseignant
     * @return projet validé
     */
    public MiniprojectResponseDTO validateMiniproject(Long projectId, Long teacherId) {
        log.info("Validation du miniprojet ID: {} par l'enseignant ID: {}", projectId, teacherId);

        Miniproject miniproject = findById(projectId);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));

        // Valider le projet
        miniproject.validate(teacher);
        miniproject = repository.save(miniproject);

        // Notifier l'étudiant
        notificationService.notifyStudentProjectValidated(miniproject);

        log.info("Miniprojet validé avec succès");
        return mapper.toResponseDTO(miniproject);
    }

    /**
     * Rejette un miniprojet
     * 
     * @param projectId ID du projet
     * @param teacherId ID de l'enseignant
     * @param feedback  raison du rejet
     * @return projet rejeté
     */
    public MiniprojectResponseDTO rejectMiniproject(Long projectId, Long teacherId, String feedback) {
        log.info("Rejet du miniprojet ID: {} par l'enseignant ID: {}", projectId, teacherId);

        Miniproject miniproject = findById(projectId);

        // Rejeter le projet
        miniproject.reject(feedback);
        miniproject = repository.save(miniproject);

        // Notifier l'étudiant
        notificationService.notifyStudentProjectRejected(miniproject);

        log.info("Miniprojet rejeté");
        return mapper.toResponseDTO(miniproject);
    }

    /**
     * Évalue un miniprojet terminé
     * 
     * @param projectId ID du projet
     * @param teacherId ID de l'enseignant
     * @param grade     note
     * @param feedback  commentaires
     * @return projet évalué
     */
    public MiniprojectResponseDTO evaluateMiniproject(Long projectId, Long teacherId, Double grade, String feedback) {
        log.info("Évaluation du miniprojet ID: {} par l'enseignant ID: {}", projectId, teacherId);

        Miniproject miniproject = findById(projectId);

        // Vérifier que l'enseignant est le superviseur
        if (!miniproject.getSupervisor().getId().equals(teacherId)) {
            throw new BusinessException("Seul le superviseur peut évaluer ce projet");
        }

        // Valider la note
        if (grade < 0 || grade > 20) {
            throw new BusinessException("La note doit être comprise entre 0 et 20");
        }

        // Évaluer le projet
        miniproject.evaluate(grade, feedback);
        miniproject = repository.save(miniproject);

        // Notifier l'étudiant
        notificationService.notifyStudentProjectEvaluated(miniproject);

        log.info("Miniprojet évalué avec succès. Note: {}", grade);
        return mapper.toResponseDTO(miniproject);
    }

    /**
     * Trouve tous les projets d'un étudiant
     * 
     * @param studentId ID de l'étudiant
     * @return liste des projets
     */
    public List<MiniprojectResponseDTO> findByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        return repository.findByStudent(student).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Trouve tous les projets supervisés par un enseignant
     * 
     * @param teacherId ID de l'enseignant
     * @param pageable  pagination
     * @return page de projets
     */
    public Page<MiniprojectResponseDTO> findBySupervisor(Long teacherId, Pageable pageable) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));

        return repository.findBySupervisor(teacher, pageable)
                .map(mapper::toResponseDTO);
    }

    /**
     * Trouve tous les projets en attente de validation
     * 
     * @param pageable pagination
     * @return page de projets
     */
    public Page<MiniprojectResponseDTO> findPendingValidation(Pageable pageable) {
        return repository.findPendingValidation(pageable)
                .map(mapper::toResponseDTO);
    }

    /**
     * Met à jour un miniprojet (uniquement en état DRAFT ou REJECTED)
     * 
     * @param projectId ID du projet
     * @param dto       données de mise à jour
     * @param studentId ID de l'étudiant
     * @return projet mis à jour
     */
    public MiniprojectResponseDTO updateMiniproject(Long projectId, MiniprojectUpdateDTO dto, Long studentId) {
        log.info("Mise à jour du miniprojet ID: {} par l'étudiant ID: {}", projectId, studentId);

        Miniproject miniproject = findById(projectId);

        // Vérifications
        if (!miniproject.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à modifier ce projet");
        }

        if (miniproject.getStatus() != ProjectStatus.DRAFT && miniproject.getStatus() != ProjectStatus.REJECTED) {
            throw new BusinessException("Le projet ne peut être modifié dans cet état");
        }

        // Mise à jour
        if (dto.getTitle() != null) {
            miniproject.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            miniproject.setDescription(dto.getDescription());
        }

        // Si le projet était rejeté, le remettre en DRAFT
        if (miniproject.getStatus() == ProjectStatus.REJECTED) {
            miniproject.setStatus(ProjectStatus.DRAFT);
            miniproject.setFeedback(null);
        }

        miniproject = repository.save(miniproject);

        log.info("Miniprojet mis à jour avec succès");
        return mapper.toResponseDTO(miniproject);
    }

    /* Ajouter un document à un projet */
    public MiniprojectResponseDTO addDocument(Long projectId,
            Long studentId,
            MultipartFile file) {

        Miniproject project = findById(projectId);

        if (!project.getStudent().getId().equals(studentId))
            throw new BusinessException("Accès refusé");

        Document doc = documentService.uploadDocument(file, project);
        project.getDocuments().add(doc);
        project = repository.save(project);

        return mapper.toResponseDTO(project);
    }

    /* Retirer un document d’un projet */
    public void removeDocument(Long projectId, Long documentId, Long studentId) {

        Miniproject project = findById(projectId);

        if (!project.getStudent().getId().equals(studentId))
            throw new BusinessException("Accès refusé");

        Document doc = project.getDocuments().stream()
                .filter(d -> d.getId().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

        project.getDocuments().remove(doc);
        documentService.deleteDocument(doc);
    }

}
