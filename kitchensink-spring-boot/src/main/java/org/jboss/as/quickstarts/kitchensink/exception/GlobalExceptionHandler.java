/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Centralizes exception handling logic for all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle bean validation errors from @Valid annotations
     * 
     * @param ex The validation exception
     * @return Map of field errors with field names as keys and error messages as values
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.debug("Handling validation exception: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.debug("Validation errors: {}", errors);
        return errors;
    }

    /**
     * Handle constraint violations from database operations
     * 
     * @param ex The constraint violation exception
     * @return ResponseEntity with error details and appropriate status code
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        log.debug("Handling constraint violation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });
        
        log.debug("Constraint violations: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle data integrity violations, such as unique constraint violations
     * 
     * @param ex The data integrity violation exception
     * @return ResponseEntity with error details and CONFLICT status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        
        // Check if it's likely an email uniqueness violation
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            errorResponse.put("email", "Email already exists");
        } else {
            errorResponse.put("error", "Database constraint violation");
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle custom validation exceptions thrown from service layer
     * 
     * @param ex The validation exception
     * @return ResponseEntity with error details and CONFLICT status
     */
    @ExceptionHandler(jakarta.validation.ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(jakarta.validation.ValidationException ex) {
        log.error("Validation exception", ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        
        // Check if it's the unique email violation from our service
        if (ex.getMessage() != null && ex.getMessage().contains("Email")) {
            errorResponse.put("email", "Email already exists");
        } else {
            errorResponse.put("error", ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Fallback handler for all other exceptions
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception", ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
