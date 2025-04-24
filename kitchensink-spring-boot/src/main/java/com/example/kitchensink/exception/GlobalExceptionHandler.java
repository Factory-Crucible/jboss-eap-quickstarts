package com.example.kitchensink.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.kitchensink.dto.ErrorResponse;
import com.example.kitchensink.dto.ValidationError;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the application.
 * This class handles exceptions thrown by controllers and converts them into
 * appropriate HTTP responses with standardized error messages.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle MethodArgumentNotValidException. This exception is thrown when @Valid
     * validation fails on a method argument.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a ResponseEntity with a detailed error response
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        List<ValidationError> validationErrors = new ArrayList<>();
        
        // Process field errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(new ValidationError(
                error.getField(),
                error.getDefaultMessage()
            ));
        }
        
        // Process global errors
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            validationErrors.add(new ValidationError(
                error.getObjectName(),
                error.getDefaultMessage()
            ));
        }
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Validation Error");
        errorResponse.setMessage("Input validation failed");
        errorResponse.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        errorResponse.setValidationErrors(validationErrors);
        
        log.error("Validation error: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ResourceNotFoundException. This exception is thrown when a requested
     * resource is not found.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a ResponseEntity with a 404 status and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError("Not Found");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        
        log.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle DuplicateResourceException. This exception is thrown when attempting
     * to create a resource that already exists.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a ResponseEntity with a 409 status and error details
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError("Conflict");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        
        log.error("Duplicate resource: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    /**
     * Handle ConstraintViolationException. This exception is thrown when a
     * constraint is violated, typically in validation.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a ResponseEntity with a 400 status and validation error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {
        
        List<ValidationError> validationErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            validationErrors.add(new ValidationError(
                violation.getPropertyPath().toString(),
                violation.getMessage()
            ));
        });
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Validation Error");
        errorResponse.setMessage("Constraint violation");
        errorResponse.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        errorResponse.setValidationErrors(validationErrors);
        
        log.error("Constraint violation: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle all other exceptions. This is a catch-all handler for any
     * exception not specifically handled above.
     *
     * @param ex      the exception
     * @param request the current request
     * @return a ResponseEntity with a 500 status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("An unexpected error occurred");
        errorResponse.setPath(((ServletWebRequest) request).getRequest().getRequestURI());
        
        log.error("Unhandled exception", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
