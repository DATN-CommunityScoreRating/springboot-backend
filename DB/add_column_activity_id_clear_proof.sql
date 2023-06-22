use community_score_bkdn;

ALTER TABLE clear_proof
ADD COLUMN user_activity_id bigint null;

ALTER TABLE clear_proof
ADD CONSTRAINT FOREIGN KEY (user_activity_id) REFERENCES user_activity(user_activity_id);

ALTER TABLE clear_proof
DROP COLUMN attach;

ALTER TABLE clear_proof
MODIFY COLUMN description TEXT NOT NULL;