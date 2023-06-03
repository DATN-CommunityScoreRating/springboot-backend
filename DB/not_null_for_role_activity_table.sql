use community_score_bkdn;
alter table activities
    modify score int not null;

alter table activities
    modify start_date timestamp not null;

alter table activities
    modify end_date timestamp not null;

alter table activities
    modify location varchar(255) not null;

alter table activities
    modify start_register timestamp not null;

alter table activities
    modify end_register timestamp not null;

alter table activities
    modify description text not null;

