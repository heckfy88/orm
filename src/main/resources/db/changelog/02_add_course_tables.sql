--liquibase formatted sql
--changeset heckfy88:2
--comment: добавлены таблицы курсов, категорий, модулей, уроков, заданий, решений, тестов, вопросов, вариантов ответов, результатов тестов, отзывов, тегов и записей на курс

create table if not exists learning.category
(
    id         uuid      default uuid_generate_v4(),
    name       varchar(100) not null unique,

    constraint pk_category primary key (id)
);

create table if not exists learning.course
(
    id          uuid      default uuid_generate_v4(),
    title       varchar(200) not null,
    description text,
    category_id uuid,
    teacher_id  uuid,
    duration    integer,
    start_date  timestamp,
    created_at  timestamp default now(),
    is_active   boolean   default true,

    constraint pk_course primary key (id),
    constraint fk_course_category foreign key (category_id) references learning.category (id),
    constraint fk_course_teacher foreign key (teacher_id) references learning.user (id)
);

create table if not exists learning.module
(
    id          uuid      default uuid_generate_v4(),
    course_id   uuid         not null,
    title       varchar(200) not null,
    description text,
    order_index integer,
    created_at  timestamp default now(),
    is_active   boolean   default true,

    constraint pk_module primary key (id),
    constraint fk_module_course foreign key (course_id) references learning.course (id)
);

create table if not exists learning.lesson
(
    id         uuid      default uuid_generate_v4(),
    module_id  uuid         not null,
    title      varchar(200) not null,
    content    text,
    video_url  varchar(500),
    created_at timestamp default now(),

    constraint pk_lesson primary key (id),
    constraint fk_lesson_module foreign key (module_id) references learning.module (id)
);

create table if not exists learning.assignment
(
    id          uuid      default uuid_generate_v4(),
    lesson_id   uuid         not null,
    title       varchar(200) not null,
    description text,
    due_date    timestamp,
    max_score   integer   default 100,
    created_at  timestamp default now(),

    constraint pk_assignment primary key (id),
    constraint fk_assignment_lesson foreign key (lesson_id) references learning.lesson (id)
);

create table if not exists learning.submission
(
    id            uuid      default uuid_generate_v4(),
    assignment_id uuid not null,
    student_id    uuid not null,
    content       text,
    submitted_at  timestamp default now(),
    score         integer,
    feedback      text,
    created_at    timestamp default now(),

    constraint pk_submission primary key (id),
    constraint fk_submission_assignment foreign key (assignment_id) references learning.assignment (id),
    constraint fk_submission_student foreign key (student_id) references learning.user (id),
    constraint uk_submission_student_assignment unique (student_id, assignment_id)
);

create table if not exists learning.quiz
(
    id         uuid      default uuid_generate_v4(),
    module_id  uuid,
    title      varchar(200) not null,
    time_limit integer,
    created_at timestamp default now(),
    is_active  boolean   default true,

    constraint pk_quiz primary key (id),
    constraint fk_quiz_module foreign key (module_id) references learning.module (id)
);

create table if not exists learning.question
(
    id         uuid      default uuid_generate_v4(),
    quiz_id    uuid        not null,
    text       text        not null,
    type       varchar(50) not null,
    created_at timestamp default now(),

    constraint pk_question primary key (id),
    constraint fk_question_quiz foreign key (quiz_id) references learning.quiz (id)
);

create table if not exists learning.answer_option
(
    id          uuid      default uuid_generate_v4(),
    question_id uuid not null,
    text        text not null,
    is_correct  boolean   default false,
    created_at  timestamp default now(),

    constraint pk_answer_option primary key (id),
    constraint fk_answer_option_question foreign key (question_id) references learning.question (id)
);

create table if not exists learning.quiz_submission
(
    id         uuid      default uuid_generate_v4(),
    quiz_id    uuid not null,
    student_id uuid not null,
    score      integer,
    taken_at   timestamp default now(),
    created_at timestamp default now(),
    is_active  boolean   default true,

    constraint pk_quiz_submission primary key (id),
    constraint fk_quiz_submission_quiz foreign key (quiz_id) references learning.quiz (id),
    constraint fk_quiz_submission_student foreign key (student_id) references learning.user (id),
    constraint uk_quiz_submission_student_quiz unique (student_id, quiz_id)
);


create table if not exists learning.course_review
(
    id         uuid      default uuid_generate_v4(),
    course_id  uuid    not null,
    student_id uuid    not null,
    rating     integer not null,
    comment    text,
    created_at timestamp default now(),

    constraint pk_course_review primary key (id),
    constraint fk_course_review_course foreign key (course_id) references learning.course (id),
    constraint fk_course_review_student foreign key (student_id) references learning.user (id),
    constraint uk_course_review_student_course unique (student_id, course_id)
);

create table if not exists learning.tag
(
    id         uuid      default uuid_generate_v4(),
    name       varchar(100) not null unique,
    created_at timestamp default now(),

    constraint pk_tag primary key (id)
);

create table if not exists learning.course_tag
(
    course_id uuid,
    tag_id    uuid,

    constraint pk_course_tag primary key (course_id, tag_id),
    constraint fk_course_tag_course foreign key (course_id) references learning.course (id),
    constraint fk_course_tag_tag foreign key (tag_id) references learning.tag (id)
);


create table if not exists learning.enrollment
(
    id          uuid        default uuid_generate_v4(),
    user_id     uuid not null,
    course_id   uuid not null,
    enroll_date timestamp   default now(),
    status      varchar(50) default 'ACTIVE',
    created_at  timestamp   default now(),

    constraint pk_enrollment primary key (id),
    constraint fk_enrollment_user foreign key (user_id) references learning.user (id),
    constraint fk_enrollment_course foreign key (course_id) references learning.course (id),
    constraint uk_enrollment_user_course unique (user_id, course_id)
);