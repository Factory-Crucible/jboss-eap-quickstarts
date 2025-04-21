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

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global exception handler for the application.
 * Provides consistent error responses across all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Handle Jakarta validation exceptions.
     *
     * @param ex the validation exception
     * @return error response with validation details
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        log.log(Level.WARNING, "Validation exception", ex);
        
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle method argument validation exceptions from @Valid annotations.
     *
     * @param ex the method argument validation exception
     * @return error response with field-specific validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.log(Level.WARNING, "Method argument validation exception", ex);
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle constraint violation exceptions from bean validation.
     *
     * @param ex the constraint violation exception
     * @return error response with constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        log.log(Level.WARNING, "Constraint violation exception", ex);
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle entity not found exceptions.
     *
     * @param ex the entity not found exception
     * @return error response for not found resources
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.log(Level.WARNING, "Entity not found exception", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle data integrity violation exceptions (e.g., unique constraint violations).
     *
     * @param ex the data integrity violation exception
     * @return error response for constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.log(Level.WARNING, "Data integrity violation exception", ex);
        
        Map<String, String> error = new HashMap<>();
        
        // Check if it's a unique constraint violation
        if (ex.getMessage().contains("unique constraint") || ex.getMessage().contains("Duplicate entry")) {
            error.put("error", "A record with the same unique identifier already exists");
            if (ex.getMessage().toLowerCase().contains("email")) {
                error.put("email", "Email address already exists");
            }
        } else {
            error.put("error", "Data integrity violation: " + ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle method argument type mismatch exceptions.
     *
     * @param ex the method argument type mismatch exception
     * @return error response for type mismatches
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.log(Level.WARNING, "Method argument type mismatch exception", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", 
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle HTTP message not readable exceptions.
     *
     * @param ex the HTTP message not readable exception
     * @return error response for malformed requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.log(Level.WARNING, "HTTP message not readable exception", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Malformed JSON request: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other exceptions.
     *
     * @param ex the exception
     * @param request the web request
     * @return error response for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.log(Level.SEVERE, "Uncaught exception", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
