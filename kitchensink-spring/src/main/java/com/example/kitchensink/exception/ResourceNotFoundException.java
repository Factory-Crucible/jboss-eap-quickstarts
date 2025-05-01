package com.example.kitchensink.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception is used to provide meaningful error messages when
 * a resource lookup operation fails.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new resource not found exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new resource not found exception with a formatted message
     * that includes the resource name, field name, and field value.
     * 
     * @param resourceName the name of the resource that was not found
     * @param fieldName the name of the field used in the lookup
     * @param fieldValue the value of the field used in the lookup
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
