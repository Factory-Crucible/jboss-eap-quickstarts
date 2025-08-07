-- ===================================================================
-- SPRING BOOT KITCHENSINK APPLICATION - DATA INITIALIZATION
-- ===================================================================
-- This file replaces the original import.sql from the JBoss EAP kitchensink
-- application. It is automatically executed by Spring Boot when the
-- application starts, after Hibernate creates the schema.
--
-- Original file: src/main/resources/import.sql
-- Migration notes: 
-- - Spring Boot uses data.sql instead of import.sql by default
-- - The SQL syntax is compatible with H2 database
-- - Column names match the Member entity (@Column annotations)
-- ===================================================================

-- Insert sample member data (same as original kitchensink application)
INSERT INTO member (name, email, phone_number) 
VALUES ('John Smith', 'john.smith@mailinator.com', '2125551212');

-- Note: We don't need to specify ID as it's auto-generated in Spring Boot
-- with GenerationType.IDENTITY strategy
