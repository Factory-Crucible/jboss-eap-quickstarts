-- Initial data for the members table
-- This script is automatically executed by Spring Boot when the application starts

-- Insert the original member from the JBoss application
INSERT INTO members (name, email, phone_number) 
VALUES ('John Smith', 'john.smith@mailinator.com', '2125551212');

-- Add some additional sample members
INSERT INTO members (name, email, phone_number) 
VALUES ('Jane Doe', 'jane.doe@mailinator.com', '2125551213');

INSERT INTO members (name, email, phone_number) 
VALUES ('Bob Johnson', 'bob.johnson@mailinator.com', '2125551214');

INSERT INTO members (name, email, phone_number) 
VALUES ('Alice Williams', 'alice.williams@mailinator.com', '2125551215');

INSERT INTO members (name, email, phone_number) 
VALUES ('Michael Brown', 'michael.brown@mailinator.com', '2125551216');
