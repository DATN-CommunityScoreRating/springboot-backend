use community_score_bkdn;

ALTER TABLE users
ADD COLUMN faculty_id bigint default null,
ADD CONSTRAINT FK_faculty_user FOREIGN KEY (faculty_id)
REFERENCES faculties(faculty_id);