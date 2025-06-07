package com.university.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.sms.config.IntegrationTestConfig;
import com.university.sms.dto.*;
import com.university.sms.dto.response.MiniprojectResponseDTO;
import com.university.sms.security.UserPrincipal;
import com.university.sms.service.MiniprojectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

/**
 * Slice WebMvc : seul le contrôleur + les beans Spring MVC
 * requis sont instanciés. La sécurité est activée mais on
 * simule un utilisateur via @WithMockUser.
 */
@SpringBootTest(classes = com.university.sms.StudentManagementSystemApplication.class)
@AutoConfigureMockMvc
@Import(IntegrationTestConfig.class)
class MiniprojectControllerTest {

        @Autowired
        MockMvc mvc;
        @Autowired
        ObjectMapper mapper;
        @MockBean
        MiniprojectService service;

        @Test
        public void postCreateProject_returns201() throws Exception {
                MiniprojectCreateDTO dto = new MiniprojectCreateDTO("Titre", "Desc");
                MiniprojectResponseDTO resp = MiniprojectResponseDTO.builder().id(1L).title("Titre").build();

                Mockito.when(service.createMiniproject(any(), anyLong(), any()))
                                .thenReturn(resp);

                MockMultipartFile projectPart = new MockMultipartFile(
                                "project",
                                "project.json",
                                MediaType.APPLICATION_JSON_VALUE,
                                mapper.writeValueAsBytes(dto));

                mvc.perform(multipart("/miniprojects")
                                .file(projectPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(SecurityMockMvcRequestPostProcessors.authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                                createTestPrincipal(), null,
                                                                createTestPrincipal().getAuthorities()))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        public static UserPrincipal createTestPrincipal() {
                return new UserPrincipal(
                                1L,
                                "student@university.com",
                                "password",
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                                true);
        }
}
