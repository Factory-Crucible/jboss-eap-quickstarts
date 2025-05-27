package com.example.kitchensink.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard error response object that will be returned to clients
 * when exceptions occur during API requests.
 * 
 * This class provides a consistent structure for all error responses,
 * including support for field-level validation errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * General error message
     */
    private String message;
    
    /**
     * Detailed error description
     */
    private String details;
    
    /**
     * Map of field-specific validation errors
     * Key: field name, Value: error message
     */
    private Map<String, String> errors;
    
    /**
     * Static factory method to create an error response with timestamp set to now
     */
    public static ErrorResponse.ErrorResponseBuilder now() {
        return ErrorResponse.builder().timestamp(LocalDateTime.now());
    }
    
    /**
     * Add a field error to the errors map
     * 
     * @param field the field name
     * @param message the error message
     * @return this ErrorResponse instance for method chaining
     */
    public ErrorResponse addFieldError(String field, String message) {
        if (errors == null) {
            errors = new HashMap<>();
        }
        errors.put(field, message);
        return this;
    }
}
