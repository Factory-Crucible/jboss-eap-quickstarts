package com.example.kitchensink.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class provides centralized exception handling for all controllers
 * and ensures a consistent error response format.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle MethodArgumentNotValidException, which occurs when @Valid validation fails on a method argument.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.now()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .details(request.getDescription(false))
                .errors(errors)
                .build();
        
        log.error("Validation error: {}", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ConstraintViolationException, which occurs when bean validation fails.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            errors.put(fieldName, violation.getMessage());
        }
        
        ErrorResponse errorResponse = ErrorResponse.now()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .details(request.getDescription(false))
                .errors(errors)
                .build();
        
        log.error("Constraint violation: {}", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle DuplicateEmailException, which occurs when attempting to register
     * a member with an email that is already in use.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with conflict status
     */
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
            DuplicateEmailException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Email already exists");
        
        ErrorResponse errorResponse = ErrorResponse.now()
                .status(HttpStatus.CONFLICT.value())
                .message("Email already in use")
                .details(request.getDescription(false))
                .errors(errors)
                .build();
        
        log.error("Duplicate email: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle DataIntegrityViolationException, which may occur due to database constraints.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with conflict status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        String message = "Data integrity violation";
        // Check if it's a unique constraint violation on email
        if (ex.getMessage().contains("email")) {
            message = "Email already in use";
        }
        
        ErrorResponse errorResponse = ErrorResponse.now()
                .status(HttpStatus.CONFLICT.value())
                .message(message)
                .details(request.getDescription(false))
                .build();
        
        log.error("Data integrity violation: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle EntityNotFoundException, which occurs when an entity is not found.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with not found status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.now()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Resource not found")
                .details(request.getDescription(false))
                .build();
        
        log.error("Entity not found: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle all other exceptions.
     * 
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with internal server error status
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.now()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .details(request.getDescription(false))
                .build();
        
        log.error("Unexpected error", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
