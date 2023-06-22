use community_score_bkdn;

alter table clear_proof
    add column start_date timestamp;

alter table clear_proof
    add column end_date timestamp;