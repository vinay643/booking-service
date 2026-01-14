-- ======================
-- USERS
-- ======================
INSERT INTO user_entity (name, email, phone, role, password) VALUES
('Admin', 'admin@homemate.com', '9999999999', 'ADMIN', 'admin123'),
('Awadesh', 'awadesh@gmail.com', '9670754878', 'USER', 'user123'),
('Amit', 'amit@gmail.com', '9876543210', 'USER', 'user123');

-- ======================
-- SERVICES
-- ======================
INSERT INTO service_entity (name, price) VALUES
('Cleaning', 500),
('Plumbing', 800),
('Electrical', 700);

-- ======================
-- PROVIDERS
-- ======================
INSERT INTO provider_entity (name, skill, available) VALUES
('Vinay', 'Cleaning', TRUE),
('Mahesh', 'Plumbing', TRUE),
('Ramesh', 'Electrical', TRUE);

-- ======================
-- BOOKINGS (optional demo)
-- ======================
INSERT INTO booking_entity
(user_name, service_name, provider_name, phone, address, status,email)
VALUES
('Awadesh', 'Cleaning', 'Vinay', '9670754878', 'Lucknow', 'ASSIGNED','vinayrajpoot64@gmail.com');
