package com.example.kitchensink.exception;

/**
 * Exception thrown when attempting to register a member with an email address
 * that is already in use by another member.
 * 
 * This exception is used in the service layer and will be handled by the
 * global exception handler to return a 409 Conflict response.
 */
public class DuplicateEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DuplicateEmailException with null as its detail message.
     */
    public DuplicateEmailException() {
        super();
    }

    /**
     * Constructs a new DuplicateEmailException with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicateEmailException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateEmailException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DuplicateEmailException with the specified cause.
     *
     * @param cause the cause
     */
    public DuplicateEmailException(Throwable cause) {
        super(cause);
    }
}
