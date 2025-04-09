package com.example.kitchensink.exception;

/**
 * Exception thrown when attempting to register or update a member with an email
 * that already exists in the database.
 * 
 * This exception replaces the ValidationException handling in the original JBoss
 * application and provides more specific error information for email uniqueness
 * constraint violations.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EmailAlreadyExistsException with null as its detail message.
     */
    public EmailAlreadyExistsException() {
        super();
    }

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
     * @param cause the cause
     */
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new EmailAlreadyExistsException with the specified cause.
     *
     * @param cause the cause
     */
    public EmailAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
