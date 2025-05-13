package com.factory.kitchensink.exception;

/**
 * Exception thrown when attempting to register a member with an email that already exists.
 * This is a runtime exception that will be handled by the global exception handler.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    /**
     * Constructs a new EmailAlreadyExistsException with the specified detail message.
     * 
     * @param message the detail message
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new EmailAlreadyExistsException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
