package org.jboss.as.quickstarts.kitchensink.web;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
