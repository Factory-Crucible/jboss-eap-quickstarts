package org.jboss.as.quickstarts.kitchensink.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.jboss.as.quickstarts.kitchensink.model.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all @RequestMapping methods.
 * Translates exceptions to appropriate HTTP responses with structured error details.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Handle validation exceptions from @Valid annotations on method arguments.
     * Maps field errors to a structured response.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed"
        ).addFieldErrors(errors);
        
        log.fine("Validation completed. violations found: " + errors.size());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violations that occur during validation.
     * Maps constraint violations to a structured response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            // For property paths like "registerMember.member.email", extract just "email"
            String fieldName = propertyPath.contains(".") ? 
                    propertyPath.substring(propertyPath.lastIndexOf(".") + 1) : 
                    propertyPath;
            
            errors.put(fieldName, violation.getMessage());
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed"
        ).addFieldErrors(errors);
        
        log.fine("Validation completed. violations found: " + errors.size());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle custom UniqueEmailException.
     * Returns a conflict response with details about the email constraint violation.
     */
    @ExceptionHandler(UniqueEmailException.class)
    public ResponseEntity<ErrorResponse> handleUniqueEmailException(
            UniqueEmailException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        ).addFieldError("email", "Email taken");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle database integrity violations.
     * This can occur when trying to insert duplicate values for unique constraints.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        
        String message = ex.getMostSpecificCause().getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        
        // Check if it's a unique constraint violation for email
        if (message.toLowerCase().contains("email")) {
            ErrorResponse errorResponse = ErrorResponse.of(
                    status.value(),
                    "Email address already exists"
            ).addFieldError("email", "Email taken");
            
            return new ResponseEntity<>(errorResponse, status);
        }
        
        // Generic data integrity violation
        ErrorResponse errorResponse = ErrorResponse.of(
                status.value(),
                "Database constraint violation"
        );
        
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handle entity not found exceptions.
     * Returns a not found response with details about the missing entity.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Catch-all handler for any unhandled exceptions.
     * Provides a generic error response to avoid exposing sensitive information.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {
        
        log.severe("Unhandled exception: " + ex.getMessage());
        ex.printStackTrace();
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
