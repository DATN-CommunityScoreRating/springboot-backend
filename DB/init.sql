use community_score_bkdn;
create table courses
(
    course_id  bigint primary key not null auto_increment,
    name       varchar(255)       not null,
    code       varchar(50)        not null unique,
    date_start timestamp          not null
);

create table faculties
(
    faculty_id   bigint primary key not null auto_increment,
    faculty_name varchar(100)       not null
);

create table activity_categories
(
    activity_category_id bigint primary key not null auto_increment,
    name                 varchar(100)       not null,
    description          varchar(255)
);

create table clear_proof_status
(
    status_id   bigint primary key not null auto_increment,
    code        varchar(50)        not null unique,
    name        varchar(100)       not null,
    description varchar(255)
);

create table roles
(
    role_id     bigint primary key not null auto_increment,
    role_name   varchar(50)        not null unique,
    description varchar(255)
);

create table clazz
(
    class_id   bigint primary key not null auto_increment,
    faculty_id bigint             not null,
    course_id  bigint             not null,
    class_name varchar(100),
    constraint FK_faculty_class foreign key (faculty_id)
        references faculties (faculty_id),
    constraint FK_course_class foreign key (course_id)
        references courses (course_id)
);

create table users
(
    user_id      bigint primary key not null auto_increment,
    class_id     bigint             null,
    student_id   varchar(100),
    username     varchar(255)       not null unique,
    password     varchar(255)       not null unique,
    full_name    varchar(255)       not null,
    email        varchar(255)       not null unique,
    avatar       varchar(255),
    score        int default 0      not null,
    phone_number varchar(12),
    role_id      bigint             not null,
    constraint FK_class_user foreign key (class_id)
        references clazz (class_id),
    constraint FK_role_user foreign key (role_id)
        references roles (role_id)
);

create table activity_subcategories
(
    activity_subcategory_id bigint primary key not null auto_increment,
    activity_category_id    bigint             not null,
    name                    varchar(255)       not null,
    description             varchar(255),
    range_score             varchar(50),
    constraint FK_category_subcategory foreign key (activity_category_id)
        references activity_categories (activity_category_id)
);

create table activities
(
    activity_id             bigint primary key not null auto_increment,
    activity_subcategory_id bigint             not null,
    name                    varchar(255)       not null,
    score                   int,
    start_date              timestamp,
    end_date                timestamp,
    location                varchar(255),
    max_quantity            int,
    constraint FK_subcategory_activity foreign key (activity_subcategory_id)
        references activity_subcategories (activity_subcategory_id)
);

create table clear_proof
(
    clear_proof_id       bigint primary key not null auto_increment,
    activity_category_id bigint             not null,
    user_id              bigint             not null,
    name                 varchar(255)       not null,
    description          varchar(255)       not null,
    attach               varchar(255)       not null,
    status_id            bigint             not null,
    constraint FK_subcategory_clear_proof foreign key (activity_category_id)
        references activity_subcategories (activity_subcategory_id),
    constraint FK_user_clear_proof foreign key (user_id)
        references users (user_id),
    constraint FK_status_clear_proof foreign key (status_id)
        references clear_proof_status (status_id)
);

create table category_hierarchy
(
    role_id        bigint not null,
    subcategory_id bigint not null,
    constraint FK_role_subcategory_hierrarchy foreign key (role_id)
        references roles (role_id),
    constraint FK_subcategory_subcategory_hierrarchy foreign key (subcategory_id)
        references activity_subcategories (activity_subcategory_id)
);


create table grading_hierarchy
(
    role_id        bigint not null,
    subcategory_id bigint not null,
    constraint FK_role_grading_hierrarchy foreign key (role_id)
        references roles (role_id),
    constraint FK_subcategory_grading_hierrarchy foreign key (subcategory_id)
        references activity_subcategories (activity_subcategory_id)
);







