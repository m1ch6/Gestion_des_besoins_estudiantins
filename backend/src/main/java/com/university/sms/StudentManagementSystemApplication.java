package com.university.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;

@SpringBootApplication
public class StudentManagementSystemApplication {
    public static void main(String[] args) {
        // System.out.println(new
        // org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
        // .encode("student123"));
        SpringApplication.run(StudentManagementSystemApplication.class, args);
    }
}