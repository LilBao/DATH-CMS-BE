
INSERT INTO branch (b_name, b_address, manage_id) VALUES 
('CGV Landmark 81', '720A Dien Bien Phu, Binh Thanh, HCMC', NULL),
('CGV Vincom Center', '72 Le Thanh Ton, District 1, HCMC', NULL);

INSERT INTO branch_phone_number (branch_id, b_phone_number) VALUES 
(1, '0123456789'),
(2, '0987654321');

INSERT INTO employee (e_user_id, e_name, sex, phone_number, email, e_password, salary, user_type, is_active, manage_id, branch_id, created_at, updated_at) VALUES 
('EMP001', 'Nguyen Van Manager', 'M', '0912345678', 'manager@cgv.vn', '$2a$10$abcdefghijklmnopqrstuv', 20000000.00, 'ADMIN', true, NULL, 1, NOW(), NOW()),
('EMP002', 'Tran Thi Staff', 'F', '0912345679', 'staff@cgv.vn', '$2a$10$abcdefghijklmnopqrstuv', 10000000.00, 'STAFF', true, 'EMP001', 1, NOW(), NOW());

UPDATE branch SET manage_id = 'EMP001' WHERE branch_id = 1;

INSERT INTO screen_room (branch_id, room_id, r_type, r_capacity) VALUES 
(1, 1, 'Standard', 100),
(1, 2, 'IMAX', 150),
(2, 1, 'Standard', 120);

INSERT INTO seat (branch_id, room_id, s_row, s_column, s_type, s_status) VALUES 
(1, 1, 1, 1, 0, true),
(1, 1, 1, 2, 0, true),
(1, 2, 1, 1, 0, true),
(1, 2, 1, 2, 0, true),
(1, 2, 2, 1, 1, true),
(1, 2, 2, 2, 1, true);

INSERT INTO genre (genre) VALUES 
('Action'),
('Comedy'),
('Drama'),
('Sci-Fi'),
('Romance');

INSERT INTO formats (f_name) VALUES 
('2D'),
('3D'),
('IMAX'),
('4DX');

INSERT INTO actor (full_name) VALUES 
('Tom Cruise'),
('Robert Downey Jr.'),
('Scarlett Johansson');

INSERT INTO movie (m_name, descript, run_time, is_dub, is_sub, release_date, closing_date, age_rating, poster_url, trailer_url) VALUES 
('Mission: Impossible', 'Action spy film', 130, false, true, '2023-07-12', '2023-08-12', 'T13', 'http://example.com/poster.jpg', 'http://example.com/trailer.mp4'),
('Iron Man', 'Superhero film', 126, false, true, '2008-05-02', '2008-06-02', 'T13', 'http://example.com/poster2.jpg', 'http://example.com/trailer2.mp4');

INSERT INTO movie_genre (movie_id, genre) VALUES 
(1, 'Action'),
(1, 'Sci-Fi'),
(2, 'Action'),
(2, 'Sci-Fi');

INSERT INTO movie_format (movie_id, f_name) VALUES 
(1, '2D'),
(1, 'IMAX'),
(2, '2D'),
(2, '3D');

INSERT INTO features (movie_id, full_name) VALUES 
(1, 'Tom Cruise'),
(2, 'Robert Downey Jr.');

INSERT INTO customer (c_user_id, c_name, sex, phone_number, email, e_password, user_type, auth_provider, provider_id, avatar_url, is_active, created_at, updated_at) VALUES 
('CUS001', 'Le Van Khach', 'M', '0923456789', 'khach@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'MEMBER', 'LOCAL', NULL, 'http://example.com/avatar.jpg', true, NOW(), NOW()),
('CUS002', 'Pham Thi Hang', 'F', '0934567890', 'hang@example.com', '$2a$10$defghijklmnopqrstuvwxy', 'MEMBER', 'LOCAL', NULL, NULL, true, NOW(), NOW());

INSERT INTO membership (point, member_rank, c_user_id) VALUES 
(100, 1, 'CUS001'),
(500, 2, 'CUS002');

INSERT INTO review (movie_id, c_user_id, rating, r_date, comment) VALUES 
(1, 'CUS001', 9, '2023-07-13', 'Great action movie!');

INSERT INTO work_shift (start_time, end_time, w_date, work) VALUES 
('08:00:00', '16:00:00', 1, 'Morning Shift - Monday'),
('16:00:00', '23:59:59', 1, 'Evening Shift - Monday');

INSERT INTO work (e_user_id, start_time, end_time, w_date) VALUES 
('EMP002', '08:00:00', '16:00:00', 1);

INSERT INTO coupon (start_date, end_date, sale_off, release_num, avail_num, is_active) VALUES 
('2023-01-01', '2023-12-31', 10, 1000, 999, true),
('2023-06-01', '2023-08-31', 20, 500, 500, true);

INSERT INTO showtime (day, start_time, end_time, f_name, movie_id, branch_id, room_id, status) VALUES 
('2023-07-15', '19:00:00', '21:10:00', 'IMAX', 1, 1, 2, 'SCHEDULED'),
('2023-07-15', '20:00:00', '22:06:00', '2D', 2, 1, 1, 'SCHEDULED');

INSERT INTO orders (order_time, payment_method, total, order_status, c_user_id, e_user_id) VALUES 
(NOW(), 'CREDIT_CARD', 200000.00, 'PAID', 'CUS001', NULL),
(NOW(), 'CASH', 150000.00, 'PAID', 'CUS002', 'EMP002');

INSERT INTO ticket (day_sold, t_price, qr_code, ticket_status, time_id, order_id, branch_id, room_id, s_row, s_column) VALUES 
('2023-07-14', 100000.00, 'QR123', 'SOLD', 1, 1, 1, 2, 1, 1),
('2023-07-14', 100000.00, 'QR124', 'SOLD', 1, 1, 1, 2, 1, 2);

INSERT INTO addon_item (price, item_type, order_id) VALUES 
(50000.00, 'FOOD_DRINK', 1),
(150000.00, 'MERCHANDISE', 2);

INSERT INTO food_drink (product_id, p_type, p_name, quantity) VALUES 
(1, 'DRINK', 'Coca Cola Large', 2);

INSERT INTO merchandise (product_id, avail_num, merch_name, start_date, end_date) VALUES 
(2, 100, 'Iron Man Action Figure', '2023-01-01', '2023-12-31');
