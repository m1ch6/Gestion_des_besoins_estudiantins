package com.university.sms;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateCorrectHashesTest {

    @Test
    public void generateCorrectHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String student = encoder.encode("student123");
        String teacher = encoder.encode("teacher123");
        String admin = encoder.encode("admin123");

        System.out.println("-- Copy these new hashes to your migration file:");
        System.out.println("Student (student123): " + student);
        System.out.println("Teacher (teacher123): " + teacher);
        System.out.println("Admin (admin123): " + admin);

        // Verify they work
        System.out.println("\n-- Verification:");
        System.out.println("Student matches: " + encoder.matches("student123", student));
        System.out.println("Teacher matches: " + encoder.matches("teacher123", teacher));
        System.out.println("Admin matches: " + encoder.matches("admin123", admin));
    }
}