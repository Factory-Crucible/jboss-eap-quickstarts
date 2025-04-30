package com.example.kitchensink.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler for REST API exceptions.
 * This class centralizes exception handling for all controllers,
 * providing consistent error responses across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions from @Valid annotations on method arguments.
     * This typically occurs when request body objects fail validation.
     * 
     * @param ex the validation exception
     * @return a map of field names to error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return errors;
    }
    
    /**
     * Handles constraint violation exceptions.
     * This typically occurs when @Validated is used on a controller class
     * or when validation is performed manually using a Validator.
     * 
     * @param ex the constraint violation exception
     * @return a map of property paths to error messages
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        
        return errors;
    }
    
    /**
     * Handles illegal argument exceptions.
     * This typically occurs when business rules are violated,
     * such as when an email is already in use.
     * 
     * @param ex the illegal argument exception
     * @return a response entity with the error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        
        // Special handling for email already exists
        if (ex.getMessage().contains("Email already exists")) {
            error.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Handles response status exceptions.
     * This typically occurs when a resource is not found or another
     * HTTP-specific error occurs.
     * 
     * @param ex the response status exception
     * @return a response entity with the error message
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Response status exception: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getReason());
        
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }
    
    /**
     * Handles all other exceptions.
     * This is a catch-all handler for exceptions that are not handled
     * by more specific handlers.
     * 
     * @param ex the exception
     * @return a response entity with the error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred. Please try again later.");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
