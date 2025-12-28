--liquibase formatted sql
--changeset heckfy88:1
--comment: добавлены таблицы пользователей

create table if not exists learning.user (
    id            uuid      default uuid_generate_v4(),
    name          varchar(100)        not null,
    email         varchar(100) unique not null,
    role          varchar(100)  not null,
    created_at    timestamp default now(),
    is_active     boolean   default true,

    constraint pk_user primary key (id)
);

create table if not exists learning.user_profile(
    id uuid default uuid_generate_v4(),
    user_id uuid not null,
    bio varchar(255),
    created_at timestamp default now(),
    is_active boolean default true,

    constraint pk_user_profile primary key (id),
    constraint fk_user_profile_user foreign key (user_id) references learning.user(id)
)