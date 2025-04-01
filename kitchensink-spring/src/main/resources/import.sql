-- Initialize the database with sample member data
-- This script is automatically executed by Spring Boot when hibernate.hbm2ddl.auto is set to create-drop

-- Insert sample members
INSERT INTO Member (id, name, email, phone_number) VALUES (1, 'John Smith', 'john.smith@example.com', '2125551212');
INSERT INTO Member (id, name, email, phone_number) VALUES (2, 'Jane Doe', 'jane.doe@example.com', '2125552323');
INSERT INTO Member (id, name, email, phone_number) VALUES (3, 'Michael Johnson', 'michael.johnson@example.com', '2125553434');
INSERT INTO Member (id, name, email, phone_number) VALUES (4, 'Sarah Williams', 'sarah.williams@example.com', '2125554545');
INSERT INTO Member (id, name, email, phone_number) VALUES (5, 'David Brown', 'david.brown@example.com', '2125555656');

-- Reset the ID sequence to continue after our manually inserted records
ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 6;
