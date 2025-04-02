--
-- Sample data initialization for the Kitchensink Spring Boot application
-- This file loads seed data into the database when the application starts
--

-- Insert a sample member
INSERT INTO Member (id, name, email, phone_number) VALUES (1, 'John Smith', 'john.smith@mailinator.com', '2125551212');

-- Additional sample members
INSERT INTO Member (id, name, email, phone_number) VALUES (2, 'Jane Doe', 'jane.doe@mailinator.com', '2125552323');
INSERT INTO Member (id, name, email, phone_number) VALUES (3, 'Bob Johnson', 'bob.johnson@mailinator.com', '2125553434');
