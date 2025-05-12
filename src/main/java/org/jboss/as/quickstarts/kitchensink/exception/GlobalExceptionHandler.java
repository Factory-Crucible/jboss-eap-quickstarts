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
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global exception handler for the application.
 * This class centralizes exception handling across all controllers
 * and provides consistent error responses to clients.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Handles EntityNotFoundException which occurs when a requested entity is not found.
     * 
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with NOT_FOUND status and error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.log(Level.WARNING, "Resource not found", ex);
        
        Map<String, String> body = new HashMap<>();
        body.put("error", "Resource not found");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ValidationException which occurs when data validation fails.
     * 
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with BAD_REQUEST status and error details
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        log.log(Level.WARNING, "Validation error", ex);
        
        Map<String, String> body = new HashMap<>();
        body.put("error", "Validation error");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions not specifically handled by other methods.
     * 
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with INTERNAL_SERVER_ERROR status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        log.log(Level.SEVERE, "Unexpected error", ex);
        
        Map<String, String> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
