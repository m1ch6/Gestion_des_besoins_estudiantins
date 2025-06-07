// src/test/java/com/university/sms/config/TestConfiguration.java
package com.university.sms.config;

import com.university.sms.security.CustomUserDetailsService;
import com.university.sms.service.NotificationService;
import com.university.sms.service.mapper.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @Primary
    public JavaMailSender mockMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    @Primary
    public NotificationService mockNotificationService() {
        return Mockito.mock(NotificationService.class);
    }

    @Bean
    @Primary
    public CustomUserDetailsService mockCustomUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public ThesisMapper mockThesisMapper() {
        return Mockito.mock(ThesisMapper.class);
    }

    @Bean
    @Primary
    public MiniprojectMapper mockMiniprojectMapper() {
        return Mockito.mock(MiniprojectMapper.class);
    }

    @Bean
    @Primary
    public StudentMapper mockStudentMapper() {
        return Mockito.mock(StudentMapper.class);
    }

    @Bean
    @Primary
    public TeacherMapper mockTeacherMapper() {
        return Mockito.mock(TeacherMapper.class);
    }

    @Bean
    @Primary
    public DocumentMapper mockDocumentMapper() {
        return Mockito.mock(DocumentMapper.class);
    }

    @Bean
    @Primary
    public DefenseMapper mockDefenseMapper() {
        return Mockito.mock(DefenseMapper.class);
    }
}
