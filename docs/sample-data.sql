-- schema-v2.sql 적용 후 실행하는 개발용 샘플 데이터
-- 모든 로컬 샘플 계정의 비밀번호: password
-- BCrypt 해시: $2a$10$vPQkIMlwttHC0vi1qqgBU.tTE105UeV7WryKN/LefQk3P9c8v1iHm
-- 실제 운영 환경에서는 이 파일을 사용하지 않습니다.

START TRANSACTION;

INSERT INTO users (id, email, role, status, last_login_at) VALUES
    (1, 'admin@fitflow.local', 'ADMIN', 'ACTIVE', '2026-07-30 08:40:00'),
    (2, 'trainer@fitflow.local', 'TRAINER', 'ACTIVE', '2026-07-30 08:50:00'),
    (3, 'member.local@fitflow.local', 'MEMBER', 'ACTIVE', '2026-07-30 09:00:00'),
    (4, 'member.google@gmail.com', 'MEMBER', 'ACTIVE', '2026-07-29 18:20:00'),
    (5, 'member.solo@fitflow.local', 'MEMBER', 'ACTIVE', NULL);

INSERT INTO user_local_credentials (user_id, login_id, password_hash) VALUES
    (1, 'admin', '$2a$10$vPQkIMlwttHC0vi1qqgBU.tTE105UeV7WryKN/LefQk3P9c8v1iHm'),
    (2, 'trainer01', '$2a$10$vPQkIMlwttHC0vi1qqgBU.tTE105UeV7WryKN/LefQk3P9c8v1iHm'),
    (3, 'member01', '$2a$10$vPQkIMlwttHC0vi1qqgBU.tTE105UeV7WryKN/LefQk3P9c8v1iHm'),
    (5, 'solo01', '$2a$10$vPQkIMlwttHC0vi1qqgBU.tTE105UeV7WryKN/LefQk3P9c8v1iHm');

INSERT INTO user_oauth_accounts (
    id, user_id, provider, provider_subject, provider_email
) VALUES
    (1, 4, 'GOOGLE', 'sample-google-subject-0001', 'member.google@gmail.com');

INSERT INTO trainers (
    id, user_id, name, phone, specialty, status
) VALUES
    (1, 2, '김도윤', '010-2000-1000', '근력 향상·자세 교정', 'ACTIVE');

INSERT INTO members (
    id, user_id, name, phone, birth_date, gender, trainer_requested, joined_at, status
) VALUES
    (1, 3, '김지훈', '010-3000-1000', '1994-08-17', 'MALE', TRUE, '2026-05-12', 'ACTIVE'),
    (2, 4, '이서연', '010-3000-2000', '1997-03-21', 'FEMALE', TRUE, '2026-06-01', 'ACTIVE'),
    (3, 5, '박민수', '010-3000-3000', '1991-11-02', 'MALE', FALSE, '2026-07-15', 'ACTIVE');

INSERT INTO trainer_assignments (
    id, member_id, trainer_id, status, started_at, ended_at, assigned_by
) VALUES
    (1, 1, 1, 'ACTIVE', '2026-05-12', NULL, 1),
    (2, 2, 1, 'ACTIVE', '2026-06-01', NULL, 1);

INSERT INTO membership_products (
    id, name, product_type, duration_days, price, pt_session_count, status
) VALUES
    (1, '1개월 자유 이용권', 'GYM', 30, 80000.00, 0, 'ACTIVE'),
    (2, '3개월 자유 이용권', 'GYM', 90, 180000.00, 0, 'ACTIVE'),
    (3, 'PT 10회 패키지', 'PT', 120, 500000.00, 10, 'ACTIVE');

INSERT INTO member_memberships (
    id, member_id, product_id, start_date, end_date, remaining_pt_sessions, status
) VALUES
    (1, 1, 2, '2026-05-12', '2026-08-09', 0, 'ACTIVE'),
    (2, 1, 3, '2026-05-12', '2026-09-08', 5, 'ACTIVE'),
    (3, 2, 1, '2026-07-01', '2026-07-30', 0, 'ACTIVE'),
    (4, 3, 1, '2026-07-15', '2026-08-13', 0, 'ACTIVE');

INSERT INTO payments (
    id, member_id, member_membership_id, amount, payment_method, status, paid_at
) VALUES
    (1, 1, 1, 180000.00, 'CARD', 'COMPLETED', '2026-05-12 10:20:00'),
    (2, 1, 2, 500000.00, 'CARD', 'PARTIALLY_REFUNDED', '2026-05-12 10:25:00'),
    (3, 2, 3, 80000.00, 'TRANSFER', 'COMPLETED', '2026-07-01 14:10:00'),
    (4, 3, 4, 80000.00, 'CASH', 'COMPLETED', '2026-07-15 11:30:00');

INSERT INTO refunds (
    id, payment_id, amount, reason, status, refunded_at, processed_by
) VALUES
    (1, 2, 30000.00, '회원 요청에 따른 PT 1회 부분 환불', 'COMPLETED', '2026-06-12 15:00:00', 1);

INSERT INTO attendances (
    id, member_id, attendance_date, checked_in_at, checked_out_at
) VALUES
    (1, 1, '2026-07-28', '2026-07-28 18:30:00', '2026-07-28 19:42:00'),
    (2, 1, '2026-07-30', '2026-07-30 09:42:00', NULL),
    (3, 2, '2026-07-30', '2026-07-30 08:10:00', '2026-07-30 09:25:00');

INSERT INTO workout_routines (
    id, member_id, trainer_id, title, description, status, start_date, end_date
) VALUES
    (1, 1, 1, '4주 근력 향상 프로그램', '주 4회 상·하체 분할 루틴', 'ACTIVE', '2026-07-20', '2026-08-16'),
    (2, 3, NULL, '개인 전신 운동', '트레이너 없이 회원이 수행하는 개인 루틴', 'ACTIVE', '2026-07-15', NULL);

INSERT INTO routine_exercises (
    id, routine_id, exercise_name, day_of_week, display_order,
    target_sets, target_reps_min, target_reps_max, target_weight, rest_seconds, memo
) VALUES
    (1, 1, '백 스쿼트', 1, 1, 4, 8, 10, 60.00, 90, '무릎과 발끝 방향 유지'),
    (2, 1, '레그 프레스', 1, 2, 4, 10, 12, 100.00, 90, NULL),
    (3, 1, '루마니안 데드리프트', 1, 3, 3, 10, 10, 50.00, 90, '허리 중립 유지'),
    (4, 2, '고블릿 스쿼트', 6, 1, 3, 12, 15, 20.00, 60, NULL);

INSERT INTO workout_sessions (
    id, member_id, routine_id, started_at, ended_at, memo
) VALUES
    (1, 1, 1, '2026-07-28 18:35:00', '2026-07-28 19:38:00', '하체 루틴 완료'),
    (2, 3, 2, '2026-07-27 10:00:00', '2026-07-27 10:45:00', '개인 운동');

INSERT INTO workout_sets (
    id, session_id, routine_exercise_id, exercise_name, set_number, weight, reps, completed
) VALUES
    (1, 1, 1, '백 스쿼트', 1, 60.00, 10, TRUE),
    (2, 1, 1, '백 스쿼트', 2, 60.00, 10, TRUE),
    (3, 1, 1, '백 스쿼트', 3, 60.00, 9, TRUE),
    (4, 1, 2, '레그 프레스', 1, 100.00, 12, TRUE),
    (5, 2, 4, '고블릿 스쿼트', 1, 20.00, 15, TRUE);

INSERT INTO equipment (
    id, name, category, location, status, purchased_at
) VALUES
    (1, '파워 랙 A', '웨이트', '1층 프리웨이트 존', 'AVAILABLE', '2024-03-10'),
    (2, '트레드밀 03', '유산소', '2층 유산소 존', 'INSPECTION', '2023-11-20'),
    (3, '레그 프레스', '웨이트', '1층 머신 존', 'AVAILABLE', '2024-01-15');

INSERT INTO equipment_maintenance_logs (
    id, equipment_id, maintenance_type, description, performed_at, performed_by, next_due_date
) VALUES
    (1, 1, 'INSPECTION', '볼트 조임과 안전바 상태 점검 완료', '2026-07-25 09:00:00', 1, '2026-08-25'),
    (2, 2, 'INSPECTION', '주행 중 소음 확인, 벨트 추가 점검 필요', '2026-07-29 16:20:00', 1, '2026-07-31'),
    (3, 3, 'CLEANING', '레일 및 시트 청소 완료', '2026-07-30 07:30:00', 2, '2026-08-06');

COMMIT;
