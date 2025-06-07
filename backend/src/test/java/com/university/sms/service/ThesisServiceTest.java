package com.university.sms.service;

import com.university.sms.dto.ThesisCreateDTO;
import com.university.sms.dto.response.ThesisResponseDTO;
import com.university.sms.entity.*;
import com.university.sms.exception.BusinessException;
import com.university.sms.repository.*;
import com.university.sms.service.mapper.ThesisMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ThesisServiceTest {

    @InjectMocks
    private ThesisService service;
    @Mock
    private ThesisRepository thesisRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private DocumentService documentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ThesisMapper mapper;

    private Student student;
    private Teacher supervisor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        supervisor = Teacher.builder().id(2L).grade("MC").department("INFO").build();

        student = Student.builder()
                .id(1L)
                .registrationNumber("23ABC")
                .supervisor(supervisor)
                .build();
        // simuler qu'il a déjà un projet évalué
        student.setMiniprojects(List.of(
                Miniproject.builder().status(ProjectStatus.EVALUATED).grade(15.0).build()));
    }

    @Test
    void createThesis_ok() {

        ThesisCreateDTO dto = ThesisCreateDTO.builder()
                .title("Titre")
                .summary("Résumé")
                .keywords(List.of("AI", "ML"))
                .build();

        Thesis thesis = Thesis.builder().id(5L).title("Titre").student(student).build();
        ThesisResponseDTO resp = ThesisResponseDTO.builder().id(5L).title("Titre").build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(thesisRepository.findByStudent(student)).thenReturn(Optional.empty());
        when(thesisRepository.existsByTitle("Titre")).thenReturn(false);
        when(thesisRepository.save(any(Thesis.class))).thenReturn(thesis);
        when(mapper.toResponse(thesis)).thenReturn(resp);

        ThesisResponseDTO result = service.createThesis(dto, 1L, mock(MultipartFile.class));

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void createThesis_withoutSupervisor_shouldThrow() {

        student.setSupervisor(null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> service.createThesis(
                new ThesisCreateDTO("T", "S", List.of("kw")), 1L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("superviseur");
    }
}
