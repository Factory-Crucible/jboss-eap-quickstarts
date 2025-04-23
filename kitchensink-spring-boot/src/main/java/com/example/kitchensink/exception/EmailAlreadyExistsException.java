package com.example.kitchensink.exception;

/**
 * Exception thrown when attempting to register a member with an email
 * that already exists in the database.
 * This exception is used to enforce the unique constraint on email addresses.
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
