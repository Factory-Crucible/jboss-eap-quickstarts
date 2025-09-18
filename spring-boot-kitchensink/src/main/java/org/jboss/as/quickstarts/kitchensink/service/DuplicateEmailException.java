package org.jboss.as.quickstarts.kitchensink.service;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) { super(message); }
}
