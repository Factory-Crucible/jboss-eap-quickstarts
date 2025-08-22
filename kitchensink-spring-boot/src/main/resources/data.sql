-- Spring Boot SQL data initialization script
-- This file loads seed data into the H2 database when the application starts
-- Used for development and testing purposes

-- Member table sample data
-- Format: id, name, email, phone_number
-- Note: With H2's default identity column, you can omit the ID and let it auto-generate
--       but we're specifying IDs here for predictable test data

-- Original member from the JBoss EAP quickstart
INSERT INTO Member (id, name, email, phone_number) VALUES (1, 'John Smith', 'john.smith@mailinator.com', '2125551212');

-- Additional members for testing
INSERT INTO Member (id, name, email, phone_number) VALUES (2, 'Jane Doe', 'jane.doe@mailinator.com', '2025559876');
INSERT INTO Member (id, name, email, phone_number) VALUES (3, 'Michael Johnson', 'michael.johnson@mailinator.com', '3305554567');
INSERT INTO Member (id, name, email, phone_number) VALUES (4, 'Sarah Williams', 'sarah.williams@mailinator.com', '4155558901');
INSERT INTO Member (id, name, email, phone_number) VALUES (5, 'Robert Brown', 'robert.brown@mailinator.com', '6175553456');

-- Note: In a production environment, you would use a proper database migration tool
-- like Flyway or Liquibase instead of this data.sql approach
