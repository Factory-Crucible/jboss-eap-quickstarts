-- Sample data for Member table
-- Names must not contain numbers, emails must be unique, phone numbers must be 10-12 digits

-- Insert test members
INSERT INTO Member (name, email, phone_number) VALUES ('John Smith', 'john@example.com', '1234567890');
INSERT INTO Member (name, email, phone_number) VALUES ('Jane Doe', 'jane@example.com', '2345678901');
INSERT INTO Member (name, email, phone_number) VALUES ('Alice Johnson', 'alice@example.com', '3456789012');
INSERT INTO Member (name, email, phone_number) VALUES ('Bob Williams', 'bob@example.com', '4567890123');
INSERT INTO Member (name, email, phone_number) VALUES ('Carol Brown', 'carol@example.com', '5678901234');

-- Additional members with international format phone numbers
INSERT INTO Member (name, email, phone_number) VALUES ('David Miller', 'david@example.com', '123456789012');
INSERT INTO Member (name, email, phone_number) VALUES ('Emma Wilson', 'emma@example.com', '234567890123');

-- Note: ID field is auto-generated (IDENTITY strategy)
