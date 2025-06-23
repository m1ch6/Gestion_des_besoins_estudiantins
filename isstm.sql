-- Adminer 4.8.1 MySQL 9.3.0 dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `defense_jury`;
CREATE TABLE `defense_jury` (
  `defense_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  PRIMARY KEY (`defense_id`,`teacher_id`),
  KEY `fk_jury_teach` (`teacher_id`),
  CONSTRAINT `fk_jury_def` FOREIGN KEY (`defense_id`) REFERENCES `defenses` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_jury_teach` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `defenses`;
CREATE TABLE `defenses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint DEFAULT NULL,
  `defense_date` datetime(6) NOT NULL,
  `location` varchar(255) NOT NULL,
  `grade` float DEFAULT NULL,
  `observations` text,
  `thesis_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_defense_thesis` (`thesis_id`),
  CONSTRAINT `fk_def_thesis` FOREIGN KEY (`thesis_id`) REFERENCES `theses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `documents`;
CREATE TABLE `documents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint DEFAULT NULL,
  `file_name` varchar(255) NOT NULL,
  `content_type` varchar(100) NOT NULL,
  `size` bigint NOT NULL,
  `storage_path` varchar(500) NOT NULL,
  `type` varchar(32) NOT NULL,
  `upload_date` datetime(6) NOT NULL,
  `miniproject_id` bigint DEFAULT NULL,
  `thesis_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_doc_miniproj` (`miniproject_id`),
  KEY `idx_doc_thesis` (`thesis_id`),
  CONSTRAINT `fk_doc_minip` FOREIGN KEY (`miniproject_id`) REFERENCES `miniprojects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_doc_thesis` FOREIGN KEY (`thesis_id`) REFERENCES `theses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES
(1,	'1',	'create tables',	'SQL',	'V1__create_tables.sql',	-219292501,	'root',	'2025-06-06 19:20:01',	1337,	1),
(2,	'2',	'insert default user',	'SQL',	'V2__insert_default_user.sql',	7706901,	'root',	'2025-06-06 22:20:45',	328,	1);

DROP TABLE IF EXISTS `miniprojects`;
CREATE TABLE `miniprojects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `student_id` bigint NOT NULL,
  `supervisor_id` bigint DEFAULT NULL,
  `status` varchar(32) NOT NULL,
  `grade` float DEFAULT NULL,
  `feedback` text,
  PRIMARY KEY (`id`),
  KEY `fk_mp_supervisor` (`supervisor_id`),
  KEY `idx_mp_status` (`status`),
  KEY `idx_mp_student` (`student_id`),
  CONSTRAINT `fk_mp_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mp_supervisor` FOREIGN KEY (`supervisor_id`) REFERENCES `teachers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint DEFAULT NULL,
  `type` varchar(32) NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `is_read` bit(1) NOT NULL DEFAULT b'0',
  `read_at` datetime(6) DEFAULT NULL,
  `recipient_id` bigint NOT NULL,
  `read` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_notif_recipient` (`recipient_id`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `id` bigint NOT NULL,
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `registration_number` varchar(50) NOT NULL,
  `promotion` varchar(50) NOT NULL,
  `speciality` varchar(100) NOT NULL,
  `supervisor_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_students_regnum` (`registration_number`),
  KEY `fk_student_superv` (`supervisor_id`),
  CONSTRAINT `fk_student_superv` FOREIGN KEY (`supervisor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `teachers`;
CREATE TABLE `teachers` (
  `id` bigint NOT NULL,
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `grade` varchar(100) NOT NULL,
  `department` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `teachers` (`id`, `full_name`, `grade`, `department`) VALUES
(2001,	'John Wick',	'Associate Professor',	'Computer Science');

DROP TABLE IF EXISTS `theses`;
CREATE TABLE `theses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `summary` text NOT NULL,
  `status` varchar(32) NOT NULL,
  `thesis_version` int NOT NULL DEFAULT '1',
  `submission_date` datetime(6) DEFAULT NULL,
  `validation_date` datetime(6) DEFAULT NULL,
  `final_grade` float DEFAULT NULL,
  `student_id` bigint NOT NULL,
  `supervisor_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_thesis_title` (`title`),
  UNIQUE KEY `uq_thesis_student` (`student_id`),
  KEY `fk_thesis_supervisor` (`supervisor_id`),
  CONSTRAINT `fk_thesis_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_thesis_supervisor` FOREIGN KEY (`supervisor_id`) REFERENCES `teachers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `thesis_keywords`;
CREATE TABLE `thesis_keywords` (
  `thesis_id` bigint NOT NULL,
  `keyword` varchar(100) NOT NULL,
  PRIMARY KEY (`thesis_id`,`keyword`),
  CONSTRAINT `fk_kw_thesis` FOREIGN KEY (`thesis_id`) REFERENCES `theses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint DEFAULT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `active` bit(1) NOT NULL,
  `role` enum('STUDENT','TEACHER','ADMINISTRATOR') NOT NULL,
  `user_type` varchar(31) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users` (`id`, `created_at`, `updated_at`, `version`, `first_name`, `last_name`, `email`, `password`, `active`, `role`, `user_type`) VALUES
(1001,	'2025-06-06 22:24:04.000000',	'2025-06-06 22:24:04.000000',	NULL,	'System',	'Admin',	'admin@university.com',	'$2a$10$U/c7Lv2ABT7OtrgvFKeDZ.1P9x6z66W7h9sGffoFt.j7aRI6q5qdi',	CONV('1', 2, 10) + 0,	'ADMINISTRATOR',	'ADMINISTRATOR'),
(2001,	'2025-06-06 22:24:04.000000',	'2025-06-06 22:24:04.000000',	NULL,	'John',	'Doe',	'teacher@university.com',	'$2a$10$USDfxIQ3gf7IfXV5eXMAFOjmOnEWy/4GtA521csmf8HH9yPM.4rfC',	CONV('1', 2, 10) + 0,	'TEACHER',	'TEACHER'),
(3001,	'2025-06-06 22:24:04.000000',	'2025-06-06 22:24:04.000000',	NULL,	'Alice',	'Smith',	'student@university.com',	'$2a$10$JiIBH8Uhbsi0M7UjwCdMIO1T9iKO3uUGMOK0cJtujni9xBz5i2v62',	CONV('1', 2, 10) + 0,	'STUDENT',	'STUDENT');

-- 2025-06-07 08:22:53
