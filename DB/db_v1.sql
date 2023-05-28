use community_score_bkdn;

ALTER TABLE faculties
ADD code varchar(20) unique;

ALTER TABLE faculties
MODIFY code varchar(20) unique not null;

ALTER TABLE clazz
MODIFY class_name varchar(100) unique not null;