-- Drop the table if it exists for clean starts
DROP TABLE IF EXISTS member;

-- Create the member table
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15)
);

-- Add constraints
ALTER TABLE member ADD CONSTRAINT check_name_length CHECK (LENGTH(name) BETWEEN 1 AND 25);
ALTER TABLE member ADD CONSTRAINT unique_email UNIQUE (email);
ALTER TABLE member ADD CONSTRAINT check_phone_number CHECK (phone_number IS NULL OR phone_number REGEXP '^\\+?\\d{10,12}$');

-- Add index on email for faster lookups
CREATE INDEX idx_member_email ON member(email);

-- Comment explaining the purpose of this script
-- This script creates the member table with all necessary constraints and indexes
-- matching the Member entity structure and validation rules.