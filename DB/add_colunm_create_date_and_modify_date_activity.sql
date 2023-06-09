use community_score_bkdn;

alter table activities
    add column create_date timestamp not null,
    add column modify_date timestamp not null;