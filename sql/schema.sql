-- ============================================================
-- TimeTrack - Database Schema
-- Time Tracking and Productivity Management System
-- ============================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS timetrack_db;
USE timetrack_db;

-- ─── Users Table ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    role            ENUM('admin', 'user') NOT NULL DEFAULT 'user',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── Time Logs Table ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS time_logs (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT             NOT NULL,
    log_date        DATE            NOT NULL,
    start_time      TIME            NOT NULL,
    end_time        TIME            NOT NULL,
    task            VARCHAR(255)    NOT NULL,
    duration        DECIMAL(5,2)    NOT NULL,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── System Settings Table ───────────────────────────────────
CREATE TABLE IF NOT EXISTS system_settings (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    setting_key     VARCHAR(50)     NOT NULL UNIQUE,
    setting_value   VARCHAR(100)    NOT NULL,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Default Data
-- ============================================================

-- Default Admin account (password: admin123)
INSERT INTO users (name, email, password, role) VALUES
    ('System Admin', 'admin@timetrack.com', 'admin123', 'admin');

-- Sample User accounts (password: password123)
INSERT INTO users (name, email, password, role) VALUES
    ('Alice Johnson', 'alice@company.com', 'password123', 'user'),
    ('Bob Smith',     'bob@company.com',   'password123', 'user'),
    ('Carol Davis',   'carol@company.com', 'password123', 'user');

-- Default System Settings
INSERT INTO system_settings (setting_key, setting_value) VALUES
    ('work_hours_per_day',  '8'),
    ('break_time_minutes',  '60'),
    ('work_days_per_week',  '5'),
    ('overtime_threshold',  '8'),
    ('start_of_day',        '09:00'),
    ('end_of_day',          '18:00');

-- ─── Sample Time Logs ────────────────────────────────────────
-- Insert sample time logs for the last few working days
INSERT INTO time_logs (user_id, log_date, start_time, end_time, task, duration) VALUES
    -- Alice's logs
    (2, CURDATE() - INTERVAL 1 DAY, '09:00', '12:00', 'Frontend Development',   3.00),
    (2, CURDATE() - INTERVAL 1 DAY, '13:00', '15:30', 'Code Review',            2.50),
    (2, CURDATE() - INTERVAL 1 DAY, '15:30', '17:00', 'Bug Fixing',             1.50),
    (2, CURDATE() - INTERVAL 2 DAY, '09:00', '11:00', 'Team Meeting',           2.00),
    (2, CURDATE() - INTERVAL 2 DAY, '11:30', '14:00', 'Backend Development',    2.50),
    (2, CURDATE() - INTERVAL 2 DAY, '14:30', '17:30', 'Testing & QA',           3.00),
    (2, CURDATE() - INTERVAL 3 DAY, '09:00', '12:30', 'Documentation',          3.50),
    (2, CURDATE() - INTERVAL 3 DAY, '13:30', '17:00', 'Frontend Development',   3.50),
    (2, CURDATE() - INTERVAL 4 DAY, '09:00', '11:00', 'Research',               2.00),
    (2, CURDATE() - INTERVAL 4 DAY, '11:30', '15:00', 'Backend Development',    3.50),
    (2, CURDATE() - INTERVAL 4 DAY, '15:30', '17:30', 'Design Review',          2.00),
    (2, CURDATE() - INTERVAL 5 DAY, '09:00', '12:00', 'Frontend Development',   3.00),
    (2, CURDATE() - INTERVAL 5 DAY, '13:00', '16:00', 'Deployment',             3.00),

    -- Bob's logs
    (3, CURDATE() - INTERVAL 1 DAY, '09:00', '11:30', 'Backend Development',    2.50),
    (3, CURDATE() - INTERVAL 1 DAY, '12:00', '15:00', 'Testing & QA',           3.00),
    (3, CURDATE() - INTERVAL 1 DAY, '15:30', '17:30', 'Documentation',          2.00),
    (3, CURDATE() - INTERVAL 2 DAY, '09:00', '12:00', 'Code Review',            3.00),
    (3, CURDATE() - INTERVAL 2 DAY, '13:00', '16:30', 'Backend Development',    3.50),
    (3, CURDATE() - INTERVAL 3 DAY, '09:00', '11:00', 'Team Meeting',           2.00),
    (3, CURDATE() - INTERVAL 3 DAY, '11:30', '14:30', 'Bug Fixing',             3.00),
    (3, CURDATE() - INTERVAL 3 DAY, '15:00', '17:00', 'Research',               2.00),
    (3, CURDATE() - INTERVAL 4 DAY, '09:00', '12:00', 'Frontend Development',   3.00),
    (3, CURDATE() - INTERVAL 4 DAY, '13:00', '17:00', 'Backend Development',    4.00),

    -- Carol's logs
    (4, CURDATE() - INTERVAL 1 DAY, '09:00', '12:00', 'Design Review',          3.00),
    (4, CURDATE() - INTERVAL 1 DAY, '13:00', '16:00', 'Frontend Development',   3.00),
    (4, CURDATE() - INTERVAL 2 DAY, '09:00', '11:30', 'Research',               2.50),
    (4, CURDATE() - INTERVAL 2 DAY, '12:00', '15:00', 'Testing & QA',           3.00),
    (4, CURDATE() - INTERVAL 2 DAY, '15:30', '17:30', 'Bug Fixing',             2.00),
    (4, CURDATE() - INTERVAL 3 DAY, '09:00', '12:30', 'Backend Development',    3.50),
    (4, CURDATE() - INTERVAL 3 DAY, '13:30', '17:00', 'Documentation',          3.50),
    (4, CURDATE() - INTERVAL 4 DAY, '09:00', '11:00', 'Team Meeting',           2.00),
    (4, CURDATE() - INTERVAL 4 DAY, '11:30', '14:00', 'Code Review',            2.50),
    (4, CURDATE() - INTERVAL 4 DAY, '14:30', '17:30', 'Deployment',             3.00);
