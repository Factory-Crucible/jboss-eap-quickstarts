-- Spring Boot kitchensink application - Sample data initialization
-- Based on the original JBoss kitchensink quickstart

-- Insert a sample member
INSERT INTO member (id, name, email, phone_number) 
VALUES (1, 'John Smith', 'john.smith@mailinator.com', '2125551212');

-- Additional sample members can be added here
INSERT INTO member (id, name, email, phone_number) 
VALUES (2, 'Jane Doe', 'jane.doe@mailinator.com', '2125552323');

INSERT INTO member (id, name, email, phone_number) 
VALUES (3, 'Alex Johnson', 'alex.johnson@mailinator.com', '2125553434');
