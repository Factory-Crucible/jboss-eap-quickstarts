package org.example.kitchensink.exception;

/**
 * Exception thrown when attempting to register a member with an email address
 * that already exists in the system.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new exception with a message containing the email that caused the conflict.
     * 
     * @param email the email address that already exists
     */
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
    
    /**
     * Constructs a new exception with a default message.
     */
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}
