package com.factory.kitchensink.exception;

public class MemberEmailExistsException extends RuntimeException {
    
    public MemberEmailExistsException(String message) {
        super(message);
    }
}
