package com.example.kitchensink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for validation error details.
 * This class represents a single validation error with information
 * about the field that failed validation and the error message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ValidationError {
    
    /**
     * The name of the field that failed validation.
     */
    private String field;
    
    /**
     * The validation error message.
     */
    private String message;
}
