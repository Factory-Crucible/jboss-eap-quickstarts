package com.factory.kitchensink.exception;

/**
 * Exception thrown when a member is not found in the system.
 * This is a runtime exception that will be handled by the global exception handler.
 */
public class MemberNotFoundException extends RuntimeException {
    
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
     * @param cause the cause of the exception
     */
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
