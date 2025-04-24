package com.example.kitchensink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to create a resource that already exists.
 * 
 * This exception is used to indicate that a resource the client is trying to create
 * already exists in the system, such as a member with the same email address.
 * When thrown, it will be translated to an HTTP 409 Conflict response.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with null as its detail message.
     */
    public DuplicateResourceException() {
        super();
    }

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method)
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateResourceException with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause The cause (which is saved for later retrieval by the getCause() method)
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DuplicateResourceException with the specified cause.
     *
     * @param cause The cause (which is saved for later retrieval by the getCause() method)
     */
    public DuplicateResourceException(Throwable cause) {
        super(cause);
    }
}
