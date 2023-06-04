use community_score_bkdn;

alter table user_activity
add CONSTRAINT uq_user_activity UNIQUE (user_id, activity_id);