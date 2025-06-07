/*
 *  Student-Management-System – schéma initial
 *  Base de données : MySQL 8 (engine InnoDB, charset utf8mb4)
 *  Pour éviter les problèmes d’ordre de création, on désactive
 *  puis ré-active les clés étrangères en fin de script.
 */
SET FOREIGN_KEY_CHECKS = 0;

-- ===================================================================
--  Table users  (racine de l’héritage SINGLE_TABLE / JOINED)
-- ===================================================================
CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    version       BIGINT,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    active        BIT(1)       NOT NULL,
    role          ENUM('STUDENT','TEACHER','ADMINISTRATOR') NOT NULL,
    user_type     VARCHAR(31)  NOT NULL,        -- Discriminator
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table students  (hérite de users)
-- ===================================================================
CREATE TABLE students (
    id                   BIGINT       NOT NULL,
    registration_number  VARCHAR(50)  NOT NULL,
    promotion            VARCHAR(50)  NOT NULL,
    speciality           VARCHAR(100) NOT NULL,
    supervisor_id        BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_students_regnum (registration_number),
    CONSTRAINT fk_student_user      FOREIGN KEY (id)            REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_student_superv    FOREIGN KEY (supervisor_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table teachers  (hérite de users)
-- ===================================================================
CREATE TABLE teachers (
    id         BIGINT       NOT NULL,
    grade      VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_teacher_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table miniprojects
-- ===================================================================
CREATE TABLE miniprojects (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    version      BIGINT,
    title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    student_id   BIGINT       NOT NULL,
    supervisor_id BIGINT,
    status       VARCHAR(32)  NOT NULL,
    grade        DECIMAL(4,2),
    feedback     TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_mp_student    FOREIGN KEY (student_id)    REFERENCES students(id)  ON DELETE CASCADE,
    CONSTRAINT fk_mp_supervisor FOREIGN KEY (supervisor_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Index pour recherches fréquentes
CREATE INDEX idx_mp_status      ON miniprojects(status);
CREATE INDEX idx_mp_student     ON miniprojects(student_id);

-- ===================================================================
--  Table theses
-- ===================================================================
CREATE TABLE theses (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    version         BIGINT,
    title           VARCHAR(255) NOT NULL,
    summary         TEXT         NOT NULL,
    status          VARCHAR(32)  NOT NULL,
    thesis_version  INT          NOT NULL DEFAULT 1,
    submission_date DATETIME(6),
    validation_date DATETIME(6),
    final_grade     DECIMAL(4,2),
    student_id      BIGINT       NOT NULL,
    supervisor_id   BIGINT       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_thesis_title    (title),
    UNIQUE KEY uq_thesis_student  (student_id),
    CONSTRAINT fk_thesis_student    FOREIGN KEY (student_id)    REFERENCES students(id)  ON DELETE CASCADE,
    CONSTRAINT fk_thesis_supervisor FOREIGN KEY (supervisor_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table thesis_keywords (collection élémentaire)
-- ===================================================================
CREATE TABLE thesis_keywords (
    thesis_id BIGINT       NOT NULL,
    keyword   VARCHAR(100) NOT NULL,
    PRIMARY KEY (thesis_id, keyword),
    CONSTRAINT fk_kw_thesis FOREIGN KEY (thesis_id) REFERENCES theses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table documents  (commune mini-projets / mémoires)
-- ===================================================================
CREATE TABLE documents (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    version       BIGINT,
    file_name     VARCHAR(255) NOT NULL,
    content_type  VARCHAR(100) NOT NULL,
    size          BIGINT       NOT NULL,
    storage_path  VARCHAR(500) NOT NULL,
    type          VARCHAR(32)  NOT NULL,
    upload_date   DATETIME(6)  NOT NULL,
    miniproject_id BIGINT,
    thesis_id     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_doc_minip FOREIGN KEY (miniproject_id) REFERENCES miniprojects(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_thesis FOREIGN KEY (thesis_id)     REFERENCES theses(id)       ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_doc_miniproj ON documents(miniproject_id);
CREATE INDEX idx_doc_thesis   ON documents(thesis_id);

-- ===================================================================
--  Table defenses
-- ===================================================================
CREATE TABLE defenses (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    version       BIGINT,
    defense_date  DATETIME(6) NOT NULL,
    location      VARCHAR(255) NOT NULL,
    grade         DECIMAL(4,2),
    observations  TEXT,
    thesis_id     BIGINT      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_defense_thesis (thesis_id),
    CONSTRAINT fk_def_thesis FOREIGN KEY (thesis_id) REFERENCES theses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table defense_jury (many-to-many defense <-> teacher)
-- ===================================================================
CREATE TABLE defense_jury (
    defense_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    PRIMARY KEY (defense_id, teacher_id),
    CONSTRAINT fk_jury_def   FOREIGN KEY (defense_id) REFERENCES defenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_jury_teach FOREIGN KEY (teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
--  Table notifications
-- ===================================================================
CREATE TABLE notifications (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    version     BIGINT,
    type        VARCHAR(32)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    message     TEXT         NOT NULL,
    is_read     BIT(1)       NOT NULL DEFAULT b'0',
    read_at     DATETIME(6),
    recipient_id BIGINT      NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_notif_recipient (recipient_id),
    CONSTRAINT fk_notif_user FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
