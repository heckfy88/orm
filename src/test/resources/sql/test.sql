create schema if not exists learning;

-- USERS
INSERT INTO learning.user (id, name, email, role, created_at, is_active)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Alice', 'alice@test.com', 'TEACHER', now(), true),
    ('22222222-2222-2222-2222-222222222222', 'Bob', 'bob@test.com', 'STUDENT', now(), true);

-- CATEGORY
INSERT INTO learning.category (id, name)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Программирование');

-- COURSE
INSERT INTO learning.course (id, title, description, category_id, teacher_id, created_at, is_active)
VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Основы Hibernate', 'Курс по ORM', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', now(), true);

-- MODULE
INSERT INTO learning.module (id, course_id, title, description, order_index, created_at, is_active)
VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Модуль 1', 'Введение', 1, now(), true);

-- QUIZ
INSERT INTO learning.quiz (id, module_id, title, time_limit, created_at, is_active)
VALUES
    ('aaaaaaaa-1111-2222-3333-aaaaaaaaaaaa', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Тест 1', 30, now(), true);

-- QUIZ_SUBMISSION
INSERT INTO learning.quiz_submission (id, quiz_id, student_id, score, taken_at, created_at, is_active)
VALUES
    ('bbbbbbbb-2222-3333-4444-bbbbbbbbbbbb', 'aaaaaaaa-1111-2222-3333-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 90, now(), now(), true);