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
package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

/**
 * Global exception handler for the application.
 * <p>
 * This class provides centralized exception handling across all controllers
 * and REST endpoints in the application.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Handle constraint violations from bean validation.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with the validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        log.log(Level.WARNING, "Validation error: {0}", ex.getMessage());
        
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
     * Handle method argument validation failures.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with the validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.log(Level.WARNING, "Method argument validation error: {0}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle custom validation exceptions.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with the validation error
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.log(Level.WARNING, "Custom validation error: {0}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        
        if (ex.getMessage().contains("Email")) {
            errors.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
        } else {
            errors.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
    }

    /**
     * Handle all other exceptions.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with the error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(
            Exception ex, WebRequest request) {
        
        log.log(Level.SEVERE, "Unexpected error occurred: {0}", ex.getMessage());
        log.log(Level.FINE, "Error details:", ex);
        
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "An unexpected error occurred. Please contact support.");
        errors.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
