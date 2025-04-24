package com.example.kitchensink.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response DTO for REST API errors.
 * This class provides a consistent structure for error responses
 * across the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * The timestamp when the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * The HTTP status code.
     */
    private int status;
    
    /**
     * The error type or category.
     */
    private String error;
    
    /**
     * A detailed error message.
     */
    private String message;
    
    /**
     * The API endpoint path where the error occurred.
     */
    private String path;
    
    /**
     * A list of validation errors, populated when the error is related to validation failures.
     */
    private List<ValidationError> validationErrors = new ArrayList<>();
}
