package org.jboss.as.quickstarts.kitchensink.exception;

/**
 * Custom exception for handling unique email constraint violations.
 * This exception is thrown when attempting to register a member with an email
 * that already exists in the system.
 */
public class UniqueEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UniqueEmailException with the default message.
     */
    public UniqueEmailException() {
        super("Email address is already in use");
    }

    /**
     * Constructs a new UniqueEmailException with the specified message.
     *
     * @param message the detail message
     */
    public UniqueEmailException(String message) {
        super(message);
    }

    /**
     * Constructs a new UniqueEmailException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public UniqueEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UniqueEmailException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public UniqueEmailException(Throwable cause) {
        super("Email address is already in use", cause);
    }
}
