package com.example.kitchensink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base exception class for member-related exceptions.
 */
public class MemberException extends RuntimeException {
    
    public MemberException(String message) {
        super(message);
    }
    
    public MemberException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when a member is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends MemberException {
    
    public MemberNotFoundException(String message) {
        super(message);
    }
    
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when attempting to register a member with an email that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends MemberException {
    
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
    
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when validation fails for a member.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MemberValidationException extends MemberException {
    
    public MemberValidationException(String message) {
        super(message);
    }
    
    public MemberValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception thrown when an operation is not permitted on a member.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class MemberOperationNotPermittedException extends MemberException {
    
    public MemberOperationNotPermittedException(String message) {
        super(message);
    }
    
    public MemberOperationNotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }
}
