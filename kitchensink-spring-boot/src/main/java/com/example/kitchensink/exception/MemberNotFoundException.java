package com.example.kitchensink.exception;

/**
 * Exception thrown when a member with a specific ID is not found in the database.
 * 
 * This exception is used when attempting to find, update, or delete a member
 * that doesn't exist in the database. It provides more specific error information
 * than a generic exception.
 */
public class MemberNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new MemberNotFoundException with null as its detail message.
     */
    public MemberNotFoundException() {
        super();
    }

    /**
     * Constructs a new MemberNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public MemberNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new MemberNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new MemberNotFoundException with the specified cause.
     *
     * @param cause the cause
     */
    public MemberNotFoundException(Throwable cause) {
        super(cause);
    }
}
