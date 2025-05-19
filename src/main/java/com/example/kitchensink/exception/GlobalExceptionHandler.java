package com.example.kitchensink.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

/**
 * Global exception handler for the application.
 * This class centralizes exception handling across all controllers
 * and provides consistent error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions for @Valid annotated parameters.
     * 
     * @param ex The validation exception
     * @return Map of field errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.debug("Handling validation exception: {}", ex.getMessage());
        
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
     * 
     * @param ex The constraint violation exception
     * @return Map of constraint violations
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        log.debug("Handling constraint violation exception: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return errors;
    }

    /**
     * Handles validation exceptions like unique constraint violations.
     * 
     * @param ex The validation exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        log.debug("Handling validation exception: {}", ex.getMessage());
        
        Map<String, String> responseObj = new HashMap<>();
        
        if (ex.getMessage().contains("Email")) {
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
        } else {
            responseObj.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
        }
    }

    /**
     * Handles entity not found exceptions.
     * 
     * @param ex The entity not found exception
     * @return ResponseEntity with error details
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class, NoResultException.class})
    public Map<String, String> handleEntityNotFound(Exception ex) {
        log.debug("Handling entity not found exception: {}", ex.getMessage());
        
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", "Entity not found");
        responseObj.put("message", ex.getMessage());
        return responseObj;
    }

    /**
     * Handles response status exceptions.
     * 
     * @param ex The response status exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        log.debug("Handling response status exception: {}", ex.getMessage());
        
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(responseObj);
    }

    /**
     * Handles all other exceptions.
     * 
     * @param ex The exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", "An unexpected error occurred");
        responseObj.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObj);
    }
}
