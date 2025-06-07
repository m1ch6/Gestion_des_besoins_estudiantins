package com.university.sms.controller;

import com.university.sms.dto.response.ThesisResponseDTO;
import com.university.sms.entity.Thesis;
import com.university.sms.security.UserPrincipal;
import com.university.sms.service.ThesisService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.university.sms.StudentManagementSystemApplication.class)
@AutoConfigureMockMvc
class ThesisControllerTest {

        @Autowired
        MockMvc mvc;

        @MockBean
        ThesisService thesisService;

        @Test
        void getThesisById_returnsThesis() throws Exception {
                Thesis thesis = new Thesis();
                thesis.setId(42L);
                thesis.setTitle("Test Thesis");

                Mockito.when(thesisService.findById(anyLong())).thenReturn(thesis);

                mvc.perform(get("/theses/42")
                                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                                .authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                new UserPrincipal(
                                                                                                1L,
                                                                                                "test@example.com",
                                                                                                "password",
                                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                                "ROLE_STUDENT")),
                                                                                                true),
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_STUDENT")))))
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(42L))
                                .andExpect(jsonPath("$.title").value("Test Thesis"));
        }
}