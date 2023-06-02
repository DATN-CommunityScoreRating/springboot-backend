use community_score_bkdn;

insert into activity_subcategories(activity_category_id, name, description, range_score)
values (1, 'Hoạt động có quy mô và thời gian từ 3 ngày trở lên', 'Hoạt động có quy mô và thời gian từ 3 ngày trở lên', '20-30'),
       (1, 'Hoạt động nhân đạo (1 đến dưới 3 ngày)', 'Hoạt động nhân đạo (1 đến dưới 3 ngày)','15-20'),
       (1, 'Hoạt động đền ơn đáp nghĩa (dưới 1 ngày)', 'Hoạt động đền ơn đáp nghĩa (dưới 1 ngày)','15'),
       (1, 'Đóng góp vật chất', 'Đóng góp vật chất', '5-10'),
       (2, 'Thành viên hoạt động quốc gia, quốc tế', 'Thành viên hoạt động quốc gia, quốc tế', '15'),
       (2, 'Thành viên hoạt động cấp thành phố', 'Thành viên hoạt động cấp thành phố', '10'),
       (2, 'Thành viên hoạt động cấp trường', 'Thành viên hoạt động cấp trường', '7'),
       (2, 'Tham gia cổ vũ hoạt động các cấp', 'Tham gia cổ vũ hoạt động các cấp', '5'),
       (2, 'Có giải cấp quốc tế', 'Có giải cấp quốc tế', '30'),
       (2, 'Có giải cấp quốc gia', 'Có giải cấp quốc gia', '25'),
       (2, 'Có giải cấp tỉnh thành phố', 'Có giải cấp tỉnh thành phố', '20'),
       (2, 'Có giải cấp DHDN', 'Có giải cấp DHDN', '15-20'),
       (2, 'Có giải cấp trường', 'Có giải cấp trường', '10-15'),
       (2, 'Có giải cấp Khoa', 'Có giải cấp Khoa', '10'),
       (2, 'Có giấy chứng nhận tham gia', 'Có giấy chứng nhận tham gia', '5'),
       (3, 'Thành viên BCH ĐHĐN trở lên, hoàn thành tốt nhiệm vụ', 'Thành viên BCH ĐHĐN trở lên, hoàn thành tốt nhiệm vụ','25'),
       (3, 'Thành viên BCH Trường, hoàn thành tốt nhiệm vụ', 'Thành viên BCH Trường, hoàn thành tốt nhiệm vụ','20'),
       (3, 'Thành viên BCH Khoa, hoàn thành tốt nhiệm vụ', 'Thành viên BCH Khoa, hoàn thành tốt nhiệm vụ','15'),
       (3, 'Thành viên BCH Lớp, hoàn thành tốt nhiệm vụ', 'Thành viên BCH Lớp, hoàn thành tốt nhiệm vụ','10'),
       (4, 'Cộng tác viên thường xuyên cho nhà trường', 'Cộng tác viên thường xuyên cho nhà trường','10-20'),
       (5, 'Tham gia gặp mặt, giao lưu, trao đổi với doanh nghiệp', 'Tham gia gặp mặt, giao lưu, trao đổi với doanh nghiệp','5-10'),
       (5, 'Tham gia tọa đàm, nói chuyện chuyên đề', 'Tham gia tọa đàm, nói chuyện chuyên đề','5-10'),
       (5, 'Tham gia các hoạt động công tác sinh viên thường niên', 'Tham gia tọa đàm, nói chuyện chuyên đề','5-10');

insert into activity_subcategories(activity_subcategory_id, activity_category_id, name, description, range_score)
values (0, 5, 'Hoạt động khác', 'Hoạt động khác', '5-30');






