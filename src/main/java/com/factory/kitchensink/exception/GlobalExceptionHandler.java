package com.factory.kitchensink.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 * This class provides centralized exception handling across all controllers.
 * It converts exceptions into standardized API error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles MemberNotFoundException.
     * 
     * @param ex the exception
     * @return a ResponseEntity with a 404 status and error details
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles EmailAlreadyExistsException.
     * 
     * @param ex the exception
     * @return a ResponseEntity with a 409 status and error details
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse("CONFLICT", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    /**
     * Handles validation exceptions.
     * Extracts field-specific validation errors from the exception.
     * 
     * @param ex the exception
     * @return a ResponseEntity with a 400 status and validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        
        ErrorResponse error = new ErrorResponse("VALIDATION_FAILED", "Validation failed", errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles all other exceptions not specifically handled elsewhere.
     * 
     * @param ex the exception
     * @return a ResponseEntity with a 500 status and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
