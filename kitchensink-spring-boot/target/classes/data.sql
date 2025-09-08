INSERT INTO member (id, name, email, phone_number) VALUES (1, 'John Smith', 'john.smith@example.com', '1234567890');
INSERT INTO member (id, name, email, phone_number) VALUES (2, 'Mary Jones', 'mary.jones@example.com', '0987654321');
ALTER TABLE member ALTER COLUMN id RESTART WITH 3;
