-- 기존 FitFlow DB에 운동 구성 그룹을 추가하는 마이그레이션입니다.
-- 기존 routine_exercises 데이터는 유지되며 workout_group_id는 우선 NULL을 허용합니다.

CREATE TABLE routine_workout_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    routine_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    week_number INT NULL,
    day_of_week TINYINT NULL,
    display_order INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_group_routine FOREIGN KEY (routine_id) REFERENCES workout_routines (id),
    CONSTRAINT ck_workout_group_week CHECK (week_number IS NULL OR week_number > 0),
    CONSTRAINT ck_workout_group_day CHECK (day_of_week IS NULL OR day_of_week BETWEEN 1 AND 7),
    CONSTRAINT ck_workout_group_order CHECK (display_order > 0),
    CONSTRAINT uk_workout_group_order UNIQUE (routine_id, display_order),
    CONSTRAINT uk_workout_group_routine_pair UNIQUE (id, routine_id),
    INDEX ix_workout_group_routine (routine_id, week_number, day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE routine_exercises
    ADD COLUMN workout_group_id BIGINT NULL AFTER routine_id,
    ADD CONSTRAINT fk_routine_exercise_group
        FOREIGN KEY (workout_group_id, routine_id) REFERENCES routine_workout_groups (id, routine_id),
    ADD INDEX ix_routine_exercise_group_order (workout_group_id, display_order);
