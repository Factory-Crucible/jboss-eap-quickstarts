package org.jboss.as.quickstarts.kitchensink.model;

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
 * Standard error response model for REST API.
 * This class provides a consistent structure for error responses across the application.
 * It includes support for general errors as well as field-level validation errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code or application-specific error code
     */
    private int status;

    /**
     * General error message
     */
    private String message;

    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Field-level validation errors
     * Key: field name, Value: error message
     */
    private Map<String, String> fieldErrors;

    /**
     * Static constructor for convenience
     * 
     * @param status HTTP status code
     * @param message Error message
     * @return New ErrorResponse instance
     */
    public static ErrorResponse of(int status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Add a field error to the response
     * 
     * @param field Field name
     * @param message Error message
     * @return This ErrorResponse instance for chaining
     */
    public ErrorResponse addFieldError(String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new HashMap<>();
        }
        fieldErrors.put(field, message);
        return this;
    }

    /**
     * Add multiple field errors to the response
     * 
     * @param errors Map of field errors (field name -> error message)
     * @return This ErrorResponse instance for chaining
     */
    public ErrorResponse addFieldErrors(Map<String, String> errors) {
        if (fieldErrors == null) {
            fieldErrors = new HashMap<>();
        }
        if (errors != null) {
            fieldErrors.putAll(errors);
        }
        return this;
    }
}
