package com.example.kitchensink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to register a member with an email that already exists.
 * This exception will be mapped to an HTTP 409 CONFLICT response.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class MemberAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new MemberAlreadyExistsException with null as its detail message.
     */
    public MemberAlreadyExistsException() {
        super();
    }

    /**
     * Constructs a new MemberAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public MemberAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new MemberAlreadyExistsException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public MemberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new MemberAlreadyExistsException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public MemberAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
