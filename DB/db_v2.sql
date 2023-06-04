use community_score_bkdn;

alter table activities
add start_register timestamp,
add end_register timestamp;

alter table activities
add description text;


create table user_activity_status (
    user_activity_status_id bigint primary key,
    status varchar(50),
    description varchar(100)
);

alter table user_activity_status
modify user_activity_status_id bigint not null primary key;

create table user_activity (
    user_activity_id bigint not null primary key,
    user_id bigint not null,
    activity_id bigint not null,
    status_id bigint not null,
    constraint FK_user_user_activity foreign key (user_id)
        references users(user_id),
    constraint FK_activity_user_activity foreign key (activity_id)
        references activities(activity_id),
    constraint FK_status_user_activity_id foreign key (status_id)
        references user_activity_status(user_activity_status_id)
);

ALTER TABLE activities
DROP FOREIGN KEY FK_subcategory_activity;

ALTER TABLE activities
DROP column  activity_subcategory_id;

ALTER TABLE community_score_bkdn.activities
ADD create_user_id bigint not null,
ADD constraint FK_user_activity FOREIGN KEY (create_user_id)
REFERENCES community_score_bkdn.users(user_id);