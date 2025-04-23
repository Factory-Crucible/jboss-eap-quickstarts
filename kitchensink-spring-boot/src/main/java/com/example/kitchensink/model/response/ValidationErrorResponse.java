package com.example.kitchensink.model.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized error response for validation errors.
 * Extends the standard ErrorResponse to include field-level validation errors.
 * Used when request body validation fails due to invalid field values.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    
    /**
     * Map of field names to error messages
     * Key: field name (e.g., "email")
     * Value: validation error message (e.g., "must be a valid email address")
     */
    private Map<String, String> errors;
    
    /**
     * Constructs a new ValidationErrorResponse with all fields.
     *
     * @param status HTTP status code
     * @param message general error message
     * @param timestamp when the error occurred
     * @param errors map of field-level validation errors
     */
    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        super(status, message, timestamp);
        this.errors = errors;
    }
}
