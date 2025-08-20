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
package org.jboss.as.quickstarts.kitchensink.springboot.exception;

import java.util.HashMap;
import java.util.Map;

import org.jboss.as.quickstarts.kitchensink.springboot.controller.MemberController.ResourceNotFoundException;
import org.jboss.as.quickstarts.kitchensink.springboot.service.MemberService.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all controllers.
 * Maps exceptions to appropriate HTTP status codes and error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles bean validation exceptions.
     * Maps field validation errors to a map of field names and error messages.
     *
     * @param ex The validation exception
     * @return Map of field names to error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.debug("Validation errors detail: {}", errors);
        return errors;
    }

    /**
     * Handles duplicate email exceptions.
     *
     * @param ex The duplicate email exception
     * @return ErrorResponse with error message
     */
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleDuplicateEmailException(DuplicateEmailException ex) {
        log.warn("Duplicate email error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Email taken");
        
        return new ErrorResponse("Email already exists", errors);
    }

    /**
     * Handles resource not found exceptions.
     *
     * @param ex The resource not found exception
     * @return ErrorResponse with error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage(), null);
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex The exception
     * @return ErrorResponse with generic error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return new ErrorResponse("An unexpected error occurred", null);
    }

    /**
     * Standard error response class for consistent error formatting.
     */
    /**
     * Simple POJO representing the error response payload.
     * Provides both a no-args constructor (for deserialisation) and a
     * convenience constructor for easy instantiation in the handlers.
     */
    public static class ErrorResponse {
        private String message;
        private Map<String, String> errors;

        public ErrorResponse() {
            // Default constructor
        }

        public ErrorResponse(String message, Map<String, String> errors) {
            this.message = message;
            this.errors = errors;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public void setErrors(Map<String, String> errors) {
            this.errors = errors;
        }
    }
}
