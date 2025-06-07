/*
 *  Seed data – default users for first login
 *  -------------------------------------------------------------
 *  E-mail / password combinations
 *    student : student@university.com  /  student123
 *    teacher : teacher@university.com  /  teacher123
 *    admin   : admin@university.com    /  admin123
 *
 *  Passwords are BCrypt-hashed (generated once with
 *  new BCryptPasswordEncoder().encode("…")).
 *
 *  IMPORTANT : update the hashes if you change the clear texts.
 */

-- -----------------------------------------------------------------
--  Disable FK checks just in case (re-enabled at the bottom)
-- -----------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

-- ================================================================
--  1)  Default TEACHER (also used as supervisor of the student)
-- ================================================================
INSERT INTO users
       (id, created_at, updated_at,
        first_name, last_name, email, password,
        active, role, user_type)
VALUES (2001, NOW(), NOW(),
        'John', 'Doe', 'teacher@university.com',
        '$2a$10$USDfxIQ3gf7IfXV5eXMAFOjmOnEWy/4GtA521csmf8HH9yPM.4rfC',  -- "teacher123"
        1, 'TEACHER', 'TEACHER');

INSERT INTO teachers
       (id, full_name, grade, department)
VALUES (2001, 'John Wick', 'Associate Professor', 'Computer Science');

-- ================================================================
--  2)  Default STUDENT  (supervised by the teacher above)
-- ================================================================
INSERT INTO users
       (id, created_at, updated_at,
        first_name, last_name, email, password,
        active, role, user_type)
VALUES (3001, NOW(), NOW(),
        'Alice', 'Smith', 'student@university.com',
        '$2a$10$JiIBH8Uhbsi0M7UjwCdMIO1T9iKO3uUGMOK0cJtujni9xBz5i2v62',  -- "student123"
        1, 'STUDENT', 'STUDENT');

INSERT INTO students
       (id, registration_number, promotion, speciality, supervisor_id)
VALUES (3001, '23A0001', 'L3', 'INFO', 2001);

-- ================================================================
--  3)  Default ADMINISTRATOR
-- ================================================================
INSERT INTO users
       (id, created_at, updated_at,
        first_name, last_name, email, password,
        active, role, user_type)
VALUES (1001, NOW(), NOW(),
        'System', 'Admin', 'admin@university.com',
        '$2a$10$U/c7Lv2ABT7OtrgvFKeDZ.1P9x6z66W7h9sGffoFt.j7aRI6q5qdi',  -- "admin123"
        1, 'ADMINISTRATOR', 'ADMINISTRATOR');

-- -----------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 1;
