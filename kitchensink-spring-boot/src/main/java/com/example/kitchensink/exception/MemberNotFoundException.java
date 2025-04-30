package com.example.kitchensink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested member cannot be found.
 * This exception will be mapped to an HTTP 404 NOT_FOUND response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends RuntimeException {

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
     * @param cause the cause of the exception
     */
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new MemberNotFoundException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public MemberNotFoundException(Throwable cause) {
        super(cause);
    }
}
