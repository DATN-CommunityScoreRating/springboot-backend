use community_score_bkdn;

alter table user_activity_status
modify column status varchar(50) not null unique;

insert into user_activity_status(user_activity_status_id, status, description)
values (2, 'SEND_PROOF', 'Đã gửi minh chứng'),
       (3, 'CONFIRMED', 'Đã xác nhận');