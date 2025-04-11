package com.factory.kitchensink.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all controllers.
 * Ensures consistent error responses and proper HTTP status codes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Error response structure for API errors.
     * Provides a consistent format for all error responses.
     */
    private record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> fieldErrors
    ) {
        private ErrorResponse(HttpStatus status, String message, String path) {
            this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, null);
        }

        private ErrorResponse(HttpStatus status, String message, String path, Map<String, String> fieldErrors) {
            this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
        }
    }

    /**
     * Handles validation exceptions thrown by @Valid annotations on method parameters.
     * Maps field-specific validation errors to a structured response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.debug("Handling validation exception: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + "; " + replacement
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles bean validation exceptions.
     * Maps constraint violations to a structured response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        log.debug("Handling constraint violation: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> getPropertyPath(violation),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles form binding exceptions.
     * Maps field binding errors to a structured response.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex, HttpServletRequest request) {
        log.debug("Handling bind exception: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing + "; " + replacement
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Form binding failed",
                request.getRequestURI(),
                fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch exceptions for request parameters.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.debug("Handling type mismatch: {}", ex.getMessage());
        
        String paramName = ex.getName();
        String message = String.format(
                "Parameter '%s' should be of type %s",
                paramName,
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put(paramName, message);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Type mismatch",
                request.getRequestURI(),
                fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles business logic exceptions (IllegalArgumentException).
     * Used for business rule violations like duplicate emails.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.debug("Handling illegal argument: {}", ex.getMessage());
        
        // Determine if this is a "not found" or "conflict" situation based on message
        HttpStatus status = ex.getMessage().contains("not found") ?
                HttpStatus.NOT_FOUND : HttpStatus.CONFLICT;

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles ResponseStatusException which is thrown manually in controllers.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        log.debug("Handling response status exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Fallback handler for all other exceptions.
     * Provides a generic error response without exposing internal details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception ex, WebRequest request, HttpServletRequest httpRequest) {
        // Log the full exception for troubleshooting
        log.error("Unhandled exception occurred", ex);
        
        // Return a generic error message to avoid exposing sensitive information
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support if the problem persists.",
                httpRequest.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts the property path from a constraint violation.
     * Removes the method name if present to get just the field name.
     */
    private String getPropertyPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        // If the path contains a method name (e.g., "registerMember.member.email"),
        // extract just the field part ("member.email" or just "email")
        int lastDotIndex = propertyPath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return propertyPath.substring(lastDotIndex + 1);
        }
        return propertyPath;
    }
}
