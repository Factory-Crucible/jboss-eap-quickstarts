package com.example.kitchensink.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for the application.
 * This class centralizes exception handling across all controllers
 * and provides consistent error responses to clients.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Handle EmailAlreadyExistsException.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with status 409 (Conflict) and error details
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex, WebRequest request) {
        
        log.warning("Email already exists: " + ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Email taken");
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    /**
     * Handle validation exceptions.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with status 400 (Bad Request) and validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warning("Validation error: " + ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle ResponseStatusException.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with the status from the exception and error details
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        
        log.warning("Response status exception: " + ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getReason());
        
        return ResponseEntity.status(ex.getStatusCode()).body(errors);
    }

    /**
     * Handle all other exceptions.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with status 500 (Internal Server Error) and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(
            Exception ex, WebRequest request) {
        
        log.severe("Unhandled exception: " + ex.getMessage());
        ex.printStackTrace();
        
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
