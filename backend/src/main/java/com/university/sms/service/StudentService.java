package com.university.sms.service;

import com.university.sms.dto.response.StudentSummaryResponse;
import com.university.sms.entity.Student;
import com.university.sms.exception.ResourceNotFoundException;
import com.university.sms.repository.StudentRepository;
import com.university.sms.service.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche métier pour les étudiants.
 * Centralise les accès au StudentRepository et applique, au besoin,
 * vos règles (unicité du matricule, validation des promotions, etc.).
 */
@Service
@Transactional
@Slf4j
public class StudentService extends BaseService<Student, StudentRepository> {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository, StudentMapper mapper) {
        super(repository);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected String getEntityName() {
        return "Student";
    }

    /*
     * ===============================================================
     * CRUD
     * ===============================================================
     */

    public Student create(Student student) {
        log.info("Création d’un étudiant : {}", student.getRegistrationNumber());
        if (repository.existsByRegistrationNumber(student.getRegistrationNumber())) {
            throw new IllegalArgumentException("Matricule déjà utilisé");
        }
        return repository.save(student);
    }

    public Student update(Long id, Student updated) {
        Student s = findById(id);
        s.setFirstName(updated.getFirstName());
        s.setLastName(updated.getLastName());
        s.setEmail(updated.getEmail());
        s.setPromotion(updated.getPromotion());
        s.setSpeciality(updated.getSpeciality());
        return repository.save(s);
    }

    /*
     * ===============================================================
     * Méthodes de recherche / listing
     * ===============================================================
     */

    public StudentSummaryResponse getSummary(Long id) {
        Student student = findById(id);
        return mapper.toSummary(student);
    }

    public StudentSummaryResponse findByRegistrationNumber(String matricule) {
        Student student = repository.findByRegistrationNumber(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
        return mapper.toSummary(student);
    }

    public List<StudentSummaryResponse> listByPromotion(String promotion) {
        return mapper.toSummaryList(repository.findByPromotion(promotion));
    }

    public List<StudentSummaryResponse> listBySpeciality(String speciality) {
        return mapper.toSummaryList(repository.findBySpeciality(speciality));
    }

    public List<StudentSummaryResponse> searchByName(String keyword) {
        // on réutilise la requête dans UserRepository via un mapper ou on fait simple :
        return mapper.toSummaryList(repository.findAll().stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName())
                        .toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList());
    }
}
