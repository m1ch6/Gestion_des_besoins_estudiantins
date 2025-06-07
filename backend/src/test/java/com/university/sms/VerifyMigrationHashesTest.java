package com.university.sms;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerifyMigrationHashesTest {

    @Test
    public void verifyMigrationPasswordHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hashes from your V2__insert_default_user.sql file
        String studentHash = "$2a$10$JiIBH8Uhbsi0M7UjwCdMIO1T9iKO3uUGMOK0cJtujni9xBz5i2v62";
        String teacherHash = "$2a$10$USDfxIQ3gf7IfXV5eXMAFOjmOnEWy/4GtA521csmf8HH9yPM.4rfC";
        String adminHash = "$2a$10$U/c7Lv2ABT7OtrgvFKeDZ.1P9x6z66W7h9sGffoFt.j7aRI6q5qdi";

        // Verify each hash matches the expected password
        assertTrue(encoder.matches("student123", studentHash),
                "Student hash should match 'student123'");
        assertTrue(encoder.matches("teacher123", teacherHash),
                "Teacher hash should match 'teacher123'");
        assertTrue(encoder.matches("admin123", adminHash),
                "Admin hash should match 'admin123'");

        System.out.println("✅ All migration password hashes are correct!");
    }
}