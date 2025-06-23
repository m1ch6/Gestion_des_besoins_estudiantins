-- Flyway migration: Initial schema for Student Management System
-- Generated from current JPA entities

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BIT(1) NOT NULL,
    role VARCHAR(31) NOT NULL,
    user_type VARCHAR(31) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE teachers (
    id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    grade VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_teacher_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE students (
    id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) NOT NULL,
    promotion VARCHAR(100) NOT NULL,
    speciality VARCHAR(100) NOT NULL,
    supervisor_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_students_regnum (registration_number),
    CONSTRAINT fk_student_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_supervisor FOREIGN KEY (supervisor_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE miniprojects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    student_id BIGINT NOT NULL,
    supervisor_id BIGINT,
    status VARCHAR(31) NOT NULL,
    grade DOUBLE,
    feedback TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_mp_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_supervisor FOREIGN KEY (supervisor_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE theses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    student_id BIGINT NOT NULL,
    supervisor_id BIGINT NOT NULL,
    status VARCHAR(31) NOT NULL,
    thesis_version BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_thesis_title (title),
    UNIQUE KEY uq_thesis_student (student_id),
    CONSTRAINT fk_thesis_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_thesis_supervisor FOREIGN KEY (supervisor_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE thesis_keywords (
    thesis_id BIGINT NOT NULL,
    keyword VARCHAR(100) NOT NULL,
    PRIMARY KEY (thesis_id, keyword),
    CONSTRAINT fk_kw_thesis FOREIGN KEY (thesis_id) REFERENCES theses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE documents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    type VARCHAR(31) NOT NULL,
    upload_date DATETIME(6) NOT NULL,
    miniproject_id BIGINT,
    thesis_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_doc_minip FOREIGN KEY (miniproject_id) REFERENCES miniprojects(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_thesis FOREIGN KEY (thesis_id) REFERENCES theses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT,
    type VARCHAR(31) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BIT(1) NOT NULL DEFAULT b'0',
    read_at DATETIME(6),
    recipient_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_notif_recipient (recipient_id),
    CONSTRAINT fk_notif_user FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE defenses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version BIGINT,
    defense_date DATETIME(6) NOT NULL,
    location VARCHAR(255) NOT NULL,
    grade DOUBLE,
    observations TEXT,
    thesis_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_defense_thesis (thesis_id),
    CONSTRAINT fk_def_thesis FOREIGN KEY (thesis_id) REFERENCES theses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE defense_jury (
    defense_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    PRIMARY KEY (defense_id, teacher_id),
    CONSTRAINT fk_jury_def FOREIGN KEY (defense_id) REFERENCES defenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_jury_teach FOREIGN KEY (teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
