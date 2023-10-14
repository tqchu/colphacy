-- Update sequence values
SELECT setval('customer_id_seq', 2);
SELECT setval('employee_id_seq', 2);
SELECT setval('role_id_seq', 2);

-- Inserted admin mustn't attached to any branches
UPDATE employee
SET branch_id = NULL;
-- Clear all branch data
DELETE
FROM branch;

INSERT INTO public.branch (closing_hour, opening_hour, phone, status, street_address, ward, district, province)
VALUES ('11:00 PM', '8:00 AM', '0356789267', 'OPEN', '127 Đồng Kè', 'Hòa Khánh Bắc', 'Liên Chiểu', 'Đà Nẵng'),
       ('11:00 PM', '8:00 AM', '0799024990', 'OPEN', 'Dương Sơn 1', 'Hòa Châu', 'Hòa Vang', 'Đà Nẵng'),
       ('11:00 PM', '8:00 AM', '0378912323', 'OPEN', '12 Nguyễn Văn Linh', 'Vĩnh Trung', 'Thanh Khê', 'Đà Nẵng'),
       ('11:00 PM', '8:00 AM', '0323449268', 'OPEN', '192 Nguyễn Lương Bằng', 'Hòa Khánh Nam', 'Liên Chiểu', 'Đà Nẵng'),
       ('11:00 PM', '8:00 AM', '0782394237', 'CLOSED', '175 Lê Hữu Trác', 'Hòa Tiến', 'Hòa Vang', 'Đà Nẵng'),
       ('11:00 PM', '8:00 AM', '0356789467', 'OPEN', '19 Nguyễn Văn Linh', 'Thạc Gián', 'Thanh Khê', 'Đà Nẵng'),
       ('11:00 PM', '8:00 AM', '0843234023', 'OPEN', '19 Lê Đại Hành', 'Chương Dương', 'Hoàn Kiếm', 'Hà Nội'),
       ('11:00 PM', '8:00 AM', '0234393243', 'OPEN', '20 Quang Trung', 'Cát Linh', 'Đống Đa', 'Hà Nội'),
       ('11:00 PM', '8:00 AM', '0323434234', 'OPEN', '19 Hai Bà Trưng', 'Tiến Thắng', 'Mê Linh', 'Hà Nội'),
       ('11:00 PM', '8:00 AM', '0334593493', 'OPEN', '20 Lý Bí', 'Phụng Châu', 'Chương Mỹ', 'Hà Nội'),
       ('11:00 PM', '8:00 AM', '0334593503', 'OPEN', '230 Lê Thánh Tông', 'Cầu Diễn', 'Từ Liêm', 'Hà Nội'),
       ('11:00 PM', '8:00 AM', '0729429532', 'OPEN', '30 Quang Trung', 'Phường 1', 'Bình Thạnh', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0792392934', 'OPEN', '56 Cách Mạng Tháng 8', 'Bình Chiểu', 'Thủ Đức', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0792032343', 'CLOSED', '30 Lương Định Của', 'Phường 1', 'Gò Vấp', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0339534903', 'OPEN', '45 Lý Thái Tổ', 'Tây Ba', 'Phú Nhuận', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0323409230', 'OPEN', '30 Trần Thánh Tông', 'Phường 2', 'Bình Thạnh', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0762394234', 'OPEN', '92 Âu Cơ', 'Tây Nhì', 'Phú Nhuận', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0792234933', 'OPEN', '20 Lạc Long Quân', 'Trung Nhất', 'Phú Nhuận', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0323959343', 'OPEN', '35 Lương Định Của', 'Trung Nhì', 'Phú Nhuận', 'TP. HCM'),
       ('11:00 PM', '8:00 AM', '0223932324', 'OPEN', '20 Trần Đại Nghĩa', 'Phú An', 'Phú Vang', 'Huế'),
       ('11:00 PM', '8:00 AM', '0792349234', 'OPEN', '19 Lê Thái Tổ', 'Giang Hải', 'Phú Lộc', 'Huế'),
       ('11:00 PM', '8:00 AM', '0794233942', 'OPEN', '33 Ngô Thì Nhậm', 'Phú Diên', 'Phú Vang', 'Huế'),
       ('11:00 PM', '8:00 AM', '0792394239', 'OPEN', '90 Ngô Văn Sở', 'Phong Dinh', 'Phong Điền', 'Huế'),
       ('11:00 PM', '8:00 AM', '0792234332', 'OPEN', '55 Ngô Sĩ Liên', 'Quảng Phước', 'Quảng Điền', 'Huế'),
       ('11:00 PM', '8:00 AM', '0323032329', 'OPEN', '13 Trần Nguyên Đáng', 'Phong Thu', 'Phong Điền', 'Huế'),
       ('11:00 PM', '8:00 AM', '0792349394', 'OPEN', '490 Nguyễn Văn Linh', 'Bình Nam', 'Thăng Bình', 'Quảng Nam'),
       ('11:00 PM', '8:00 AM', '0345093490', 'OPEN', '19 Phạm Văn Đồng', 'TT. Hà Lam', 'Thăng Bình', 'Quảng Nam'),
       ('11:00 PM', '8:00 AM', '0792394329', 'OPEN', '24 Lê Duẩn', 'Duy Hải', 'Duy Xuyên', 'Quảng Nam'),
       ('11:00 PM', '8:00 AM', '0334954555', 'OPEN', '23 Lê Đức Thọ', 'Duy Hòa', 'Duy Xuyên', 'Quảng Nam'),
       ('11:00 PM', '8:00 AM', '0792340230', 'OPEN', '49 Lê Lai', 'Đại Sơn', 'Đại Lộc', 'Quảng Nam')