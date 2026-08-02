-- FitFlow MySQL 8.0+ 기준 스키마
-- 회원 자기 가입(LOCAL/GOOGLE), 선택적 트레이너 배정, 회원권/결제/출석,
-- 운동 루틴/기록, 시설 관리를 포함한 MVP 구조

DROP TABLE IF EXISTS equipment_maintenance_logs;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS workout_sets;
DROP TABLE IF EXISTS workout_sessions;
DROP TABLE IF EXISTS routine_exercises;
DROP TABLE IF EXISTS workout_routines;
DROP TABLE IF EXISTS attendances;
DROP TABLE IF EXISTS refunds;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS member_memberships;
DROP TABLE IF EXISTS membership_products;
DROP TABLE IF EXISTS trainer_assignments;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS user_oauth_accounts;
DROP TABLE IF EXISTS user_local_credentials;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PROFILE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'TRAINER', 'MEMBER')),
    CONSTRAINT ck_users_status CHECK (status IN ('PENDING_PROFILE', 'ACTIVE', 'SUSPENDED', 'WITHDRAWN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_local_credentials (
    user_id BIGINT PRIMARY KEY,
    login_id VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    password_changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_local_login_id UNIQUE (login_id),
    CONSTRAINT fk_local_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_oauth_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    provider VARCHAR(30) NOT NULL,
    provider_subject VARCHAR(255) NOT NULL,
    provider_email VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_oauth_provider_subject UNIQUE (provider, provider_subject),
    CONSTRAINT uk_oauth_user_provider UNIQUE (user_id, provider),
    CONSTRAINT fk_oauth_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_oauth_provider CHECK (provider IN ('GOOGLE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    birth_date DATE NULL,
    gender VARCHAR(20) NULL,
    trainer_requested BOOLEAN NOT NULL DEFAULT FALSE,
    joined_at DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_members_user UNIQUE (user_id),
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_members_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'WITHDRAWN')),
    CONSTRAINT ck_members_gender CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE')),
    INDEX ix_members_name (name),
    INDEX ix_members_phone (phone),
    INDEX ix_members_trainer_requested (trainer_requested, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE trainers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    specialty VARCHAR(255) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_trainers_user UNIQUE (user_id),
    CONSTRAINT fk_trainers_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_trainers_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    INDEX ix_trainers_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE trainer_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    started_at DATE NOT NULL,
    ended_at DATE NULL,
    assigned_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_assignment_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id),
    CONSTRAINT fk_assignment_admin FOREIGN KEY (assigned_by) REFERENCES users (id),
    CONSTRAINT ck_assignment_status CHECK (status IN ('ACTIVE', 'ENDED')),
    CONSTRAINT ck_assignment_period CHECK (ended_at IS NULL OR ended_at >= started_at),
    INDEX ix_assignment_member_status (member_id, status),
    INDEX ix_assignment_trainer_status (trainer_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE membership_products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    product_type VARCHAR(30) NOT NULL,
    duration_days INT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    pt_session_count INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_membership_products_name UNIQUE (name),
    CONSTRAINT ck_product_type CHECK (product_type IN ('GYM', 'PT', 'COMBINED')),
    CONSTRAINT ck_product_duration CHECK (duration_days > 0),
    CONSTRAINT ck_product_price CHECK (price >= 0),
    CONSTRAINT ck_product_pt_count CHECK (pt_session_count >= 0),
    CONSTRAINT ck_product_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE member_memberships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    remaining_pt_sessions INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_membership_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_member_membership_product FOREIGN KEY (product_id) REFERENCES membership_products (id),
    CONSTRAINT ck_member_membership_period CHECK (end_date >= start_date),
    CONSTRAINT ck_remaining_pt_sessions CHECK (remaining_pt_sessions >= 0),
    CONSTRAINT ck_member_membership_status CHECK (
        status IN ('PENDING_PAYMENT', 'ACTIVE', 'PAUSED', 'EXPIRED', 'CANCELLED')
    ),
    INDEX ix_member_membership_status (member_id, status),
    INDEX ix_member_membership_end_date (end_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    member_membership_id BIGINT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    paid_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_payment_membership FOREIGN KEY (member_membership_id) REFERENCES member_memberships (id),
    CONSTRAINT ck_payment_amount CHECK (amount > 0),
    CONSTRAINT ck_payment_method CHECK (payment_method IN ('CARD', 'CASH', 'TRANSFER')),
    CONSTRAINT ck_payment_status CHECK (status IN ('PENDING', 'COMPLETED', 'PARTIALLY_REFUNDED', 'REFUNDED', 'CANCELLED')),
    INDEX ix_payments_member_paid_at (member_id, paid_at),
    INDEX ix_payments_membership (member_membership_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE refunds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    reason VARCHAR(500) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    refunded_at DATETIME NOT NULL,
    processed_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES payments (id),
    CONSTRAINT fk_refund_admin FOREIGN KEY (processed_by) REFERENCES users (id),
    CONSTRAINT ck_refund_amount CHECK (amount > 0),
    CONSTRAINT ck_refund_status CHECK (status IN ('PENDING', 'COMPLETED', 'REJECTED')),
    INDEX ix_refunds_payment (payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE attendances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    checked_in_at DATETIME NOT NULL,
    checked_out_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT ck_attendance_time CHECK (checked_out_at IS NULL OR checked_out_at >= checked_in_at),
    INDEX ix_attendance_member_date (member_id, attendance_date),
    INDEX ix_attendance_open (member_id, checked_out_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE workout_routines (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    trainer_id BIGINT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE NOT NULL,
    end_date DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_routine_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_routine_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id),
    CONSTRAINT ck_routine_status CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_routine_period CHECK (end_date IS NULL OR end_date >= start_date),
    INDEX ix_routine_member_status (member_id, status),
    INDEX ix_routine_trainer_status (trainer_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE routine_exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    routine_id BIGINT NOT NULL,
    exercise_name VARCHAR(150) NOT NULL,
    day_of_week TINYINT NULL,
    display_order INT NOT NULL DEFAULT 1,
    target_sets INT NOT NULL,
    target_reps_min INT NULL,
    target_reps_max INT NULL,
    target_weight DECIMAL(8, 2) NULL,
    rest_seconds INT NULL,
    memo VARCHAR(500) NULL,
    CONSTRAINT fk_routine_exercise_routine FOREIGN KEY (routine_id) REFERENCES workout_routines (id),
    CONSTRAINT ck_exercise_day CHECK (day_of_week IS NULL OR day_of_week BETWEEN 1 AND 7),
    CONSTRAINT ck_exercise_order CHECK (display_order > 0),
    CONSTRAINT ck_exercise_sets CHECK (target_sets > 0),
    CONSTRAINT ck_exercise_reps CHECK (
        target_reps_min IS NULL OR target_reps_max IS NULL OR target_reps_max >= target_reps_min
    ),
    INDEX ix_routine_exercise_order (routine_id, day_of_week, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE workout_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    routine_id BIGINT NULL,
    started_at DATETIME NOT NULL,
    ended_at DATETIME NULL,
    memo VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_session_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_workout_session_routine FOREIGN KEY (routine_id) REFERENCES workout_routines (id),
    CONSTRAINT ck_workout_session_time CHECK (ended_at IS NULL OR ended_at >= started_at),
    INDEX ix_workout_session_member_date (member_id, started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE workout_sets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    routine_exercise_id BIGINT NULL,
    exercise_name VARCHAR(150) NOT NULL,
    set_number INT NOT NULL,
    weight DECIMAL(8, 2) NULL,
    reps INT NULL,
    completed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_set_session FOREIGN KEY (session_id) REFERENCES workout_sessions (id),
    CONSTRAINT fk_workout_set_routine_exercise FOREIGN KEY (routine_exercise_id) REFERENCES routine_exercises (id),
    CONSTRAINT ck_workout_set_number CHECK (set_number > 0),
    CONSTRAINT ck_workout_set_weight CHECK (weight IS NULL OR weight >= 0),
    CONSTRAINT ck_workout_set_reps CHECK (reps IS NULL OR reps >= 0),
    CONSTRAINT uk_workout_set_number UNIQUE (session_id, exercise_name, set_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NULL,
    location VARCHAR(100) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    purchased_at DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_equipment_status CHECK (status IN ('AVAILABLE', 'INSPECTION', 'REPAIR', 'UNAVAILABLE')),
    INDEX ix_equipment_status (status),
    INDEX ix_equipment_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE equipment_maintenance_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL,
    maintenance_type VARCHAR(30) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    performed_at DATETIME NOT NULL,
    performed_by BIGINT NOT NULL,
    next_due_date DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maintenance_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id),
    CONSTRAINT fk_maintenance_user FOREIGN KEY (performed_by) REFERENCES users (id),
    CONSTRAINT ck_maintenance_type CHECK (maintenance_type IN ('CLEANING', 'INSPECTION', 'REPAIR')),
    INDEX ix_maintenance_equipment_date (equipment_id, performed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


ALTER TABLE members
    ADD COLUMN gender VARCHAR(20) NULL AFTER birth_date;
-- 애플리케이션에서 추가로 보장할 규칙
-- 1. 회원별 ACTIVE 트레이너 배정은 최대 1건
-- 2. 회원별 checked_out_at IS NULL인 출석은 최대 1건
-- 3. 환불 완료 금액 합계는 원 결제 금액을 초과할 수 없음
-- 4. 회원권 활성화와 결제 완료 처리는 하나의 트랜잭션으로 수행
