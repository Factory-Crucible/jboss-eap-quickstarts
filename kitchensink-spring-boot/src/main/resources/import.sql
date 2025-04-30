-- Initialize the database with sample member data
-- This script runs automatically when the application starts due to spring.jpa.defer-datasource-initialization=true

-- Original member from the JBoss application
INSERT INTO Member (id, name, email, phone_number) VALUES (1, 'John Smith', 'john.smith@mailinator.com', '2125551212');

-- Additional sample members to showcase the application
INSERT INTO Member (id, name, email, phone_number) VALUES (2, 'Jane Doe', 'jane.doe@mailinator.com', '2025559876');
INSERT INTO Member (id, name, email, phone_number) VALUES (3, 'Alice Johnson', 'alice.johnson@mailinator.com', '7185551234');
INSERT INTO Member (id, name, email, phone_number) VALUES (4, 'Bob Williams', 'bob.williams@mailinator.com', '3475556789');
INSERT INTO Member (id, name, email, phone_number) VALUES (5, 'Carol Taylor', 'carol.taylor@mailinator.com', '9175554321');
