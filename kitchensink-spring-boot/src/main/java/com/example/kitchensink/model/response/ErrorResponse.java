package com.example.kitchensink.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response model for REST API.
 * Used to provide consistent error information when exceptions occur.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * HTTP status code of the error
     */
    private int status;
    
    /**
     * Error message describing what went wrong
     */
    private String message;
    
    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;
}
