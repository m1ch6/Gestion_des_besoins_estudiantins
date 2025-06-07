package com.university.sms.service;

import com.university.sms.dto.MiniprojectCreateDTO;
import com.university.sms.dto.response.MiniprojectResponseDTO;
import com.university.sms.entity.*;
import com.university.sms.exception.BusinessException;
import com.university.sms.repository.*;
import com.university.sms.service.mapper.MiniprojectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires basés sur Mockito : on isole la couche Service
 * en simulant les repositories, le mapper et les sous-services.
 */
class MiniprojectServiceTest {

    @InjectMocks
    private MiniprojectService service;
    @Mock
    private MiniprojectRepository repository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private DocumentService documentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MiniprojectMapper mapper;

    private Student student;
    private Miniproject project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = Student.builder()
                .id(1L)
                .registrationNumber("23ABC")
                .promotion("L3")
                .speciality("INFO")
                .build();

        project = Miniproject.builder()
                .id(10L)
                .title("Demo")
                .description("Desc")
                .student(student)
                .status(ProjectStatus.DRAFT)
                .build();
    }

    /* -------------------------------------------------- */
    /* createMiniproject – succès */
    /* -------------------------------------------------- */
    @Test
    void givenValidDTO_whenCreate_thenReturnResponse() {

        MiniprojectCreateDTO dto = new MiniprojectCreateDTO("Demo", "Desc");
        MiniprojectResponseDTO expected = MiniprojectResponseDTO.builder().id(10L).title("Demo").build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(repository.existsByTitleAndStudent("Demo", student)).thenReturn(false);
        when(repository.save(any(Miniproject.class))).thenReturn(project);
        when(mapper.toResponseDTO(project)).thenReturn(expected);

        MiniprojectResponseDTO result = service.createMiniproject(dto, 1L, List.of());

        assertThat(result.getId()).isEqualTo(10L);
        verify(notificationService, never()).notifyTeachersNewProject(any());
    }

    /* -------------------------------------------------- */
    /* submitMiniproject – pas propriétaire */
    /* -------------------------------------------------- */
    @Test
    void givenWrongStudent_whenSubmit_thenBusinessException() {

        project.getDocuments().add(Document.builder().id(99L).build());

        when(repository.findById(10L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.submitMiniproject(10L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("autorisé");
    }
}
