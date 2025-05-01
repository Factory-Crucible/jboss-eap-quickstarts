package com.example.kitchensink.exception;

/**
 * Exception thrown when attempting to register or update a member with an email
 * that already exists in the system. This exception is used to enforce the uniqueness
 * constraint on member email addresses.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new email already exists exception with the specified detail message.
     * 
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
