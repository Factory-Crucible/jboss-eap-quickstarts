package com.example.kitchensink.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class centralizes exception handling across all controllers
 * and provides consistent error responses.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handles validation exceptions for request body parameters.
     * This occurs when @Valid annotation is used on request bodies.
     * 
     * @param ex the validation exception
     * @return a map of field names to error messages with 400 status code
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    /**
     * Handles constraint violation exceptions.
     * This occurs when validation fails for method parameters.
     * 
     * @param ex the constraint violation exception
     * @return a map of property paths to error messages with 400 status code
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        
        log.error("Constraint violation: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    /**
     * Handles illegal argument exceptions.
     * This is used for business logic validation errors.
     * 
     * @param ex the illegal argument exception
     * @return an error response with 409 status code for conflicts
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.error("Business validation error: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        
        // Check for specific error messages to determine appropriate response
        String message = ex.getMessage();
        if (message != null && message.contains("Email already exists")) {
            error.put("email", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } else if (message != null && message.contains("not found")) {
            error.put("error", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else {
            error.put("error", message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * Handles all other unhandled exceptions.
     * 
     * @param ex the exception
     * @return a generic error response with 500 status code
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unhandled exception", ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
