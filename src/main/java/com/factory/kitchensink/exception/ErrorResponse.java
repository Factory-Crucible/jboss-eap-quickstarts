package com.factory.kitchensink.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response structure for API errors.
 * This class provides a consistent format for error responses across the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Error code identifying the type of error.
     */
    private String code;
    
    /**
     * Human-readable error message.
     */
    private String message;
    
    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Field-specific validation errors.
     * Key is the field name, value is the error message.
     */
    private Map<String, String> errors = new HashMap<>();
    
    /**
     * Constructs a new ErrorResponse with the specified code and message.
     * 
     * @param code the error code
     * @param message the error message
     */
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    /**
     * Constructs a new ErrorResponse with the specified code, message, and field errors.
     * 
     * @param code the error code
     * @param message the error message
     * @param errors the field-specific errors
     */
    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}
