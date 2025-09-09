package org.jboss.as.quickstarts.kitchensink.spring.web;

import org.jboss.as.quickstarts.kitchensink.spring.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MemberService.UniqueEmailViolation.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(MemberService.UniqueEmailViolation ex) {
        Map<String, String> body = new HashMap<>();
        body.put("email", "Email taken");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
