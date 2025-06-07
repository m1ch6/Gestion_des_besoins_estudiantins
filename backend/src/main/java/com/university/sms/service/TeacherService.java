package com.university.sms.service;

import com.university.sms.dto.response.TeacherSummaryResponse;
import com.university.sms.entity.Teacher;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.repository.TeacherRepository;
import com.university.sms.service.mapper.TeacherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche métier pour la gestion des enseignants.
 *
 * NB : Les contrôleurs métier (Miniproject / Thesis) accèdent parfois
 * directement au TeacherRepository. Ce service vous permettra de
 * centraliser la logique et d’exposer, plus tard, une API dédiée
 * (CRUD enseignant, recherche, statistiques…).
 */
@Service
@Transactional
@Slf4j
public class TeacherService extends BaseService<Teacher, TeacherRepository> {

    private final TeacherMapper mapper;

    public TeacherService(TeacherRepository repository, TeacherMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected String getEntityName() {
        return "Teacher";
    }

    /*
     * ===============================================================
     * CRUD
     * ===============================================================
     */

    public Teacher create(Teacher teacher) {
        log.info("Création d’un enseignant : {}", teacher.getEmail());
        return repository.save(teacher);
    }

    public Teacher update(Long id, Teacher updated) {
        Teacher t = findById(id);
        t.setFirstName(updated.getFirstName());
        t.setLastName(updated.getLastName());
        t.setEmail(updated.getEmail());
        t.setGrade(updated.getGrade());
        t.setDepartment(updated.getDepartment());
        return repository.save(t);
    }

    /*
     * ===============================================================
     * Méthodes de recherche / listing
     * ===============================================================
     */

    public List<TeacherSummaryResponse> listAllSummaries() {
        return mapper.toSummaryList(repository.findAll());
    }

    public List<TeacherSummaryResponse> findByDepartment(String department) {
        return mapper.toSummaryList(repository.findByDepartment(department));
    }

    public List<TeacherSummaryResponse> searchByName(String keyword) {
        return mapper.toSummaryList(repository.searchByName(keyword));
    }

    public TeacherSummaryResponse getSummary(Long id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));
        return mapper.toSummary(teacher);
    }
}
