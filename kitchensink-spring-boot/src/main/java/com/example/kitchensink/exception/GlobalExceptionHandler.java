package com.example.kitchensink.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class uses Spring's @ControllerAdvice to handle exceptions across the application.
 * It provides consistent error responses with appropriate HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle ConstraintViolationException (Bean Validation errors).
     * Maps each constraint violation to its property path and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with status 400 and validation errors in the body
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle ValidationException (business validation errors).
     * Returns a 409 Conflict response for email uniqueness violations,
     * or a 400 Bad Request for other validation errors.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with appropriate status and error message
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Business validation error: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Check if it's an email uniqueness violation
        if (ex.getMessage().contains("Email already exists") || 
            ex.getMessage().contains("Email taken")) {
            error.put("email", "Email taken");
            status = HttpStatus.CONFLICT;
        } else {
            error.put("error", ex.getMessage());
        }
        
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Handle EntityNotFoundException.
     * Returns a 404 Not Found response.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with status 404 and error message
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {
        
        log.warn("Entity not found: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle all other exceptions.
     * Returns a 500 Internal Server Error response.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with status 500 and error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Unhandled exception", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
