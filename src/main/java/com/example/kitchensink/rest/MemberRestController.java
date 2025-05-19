package com.example.kitchensink.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberRegistrationService;

import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

/**
 * REST controller for managing members.
 * This class provides a RESTful API to create and retrieve members.
 * It replaces the JAX-RS implementation from the original JBoss application.
 */
@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);

    private final MemberRepository memberRepository;
    private final MemberRegistrationService memberRegistration;

    @Autowired
    public MemberRestController(MemberRepository memberRepository, MemberRegistrationService memberRegistration) {
        this.memberRepository = memberRepository;
        this.memberRegistration = memberRegistration;
    }

    /**
     * Lists all members.
     * 
     * @return List of all members ordered by name
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Looks up a member by ID.
     * 
     * @param id The ID of the member to find
     * @return The member with the given ID
     * @throws ResponseStatusException if no member is found with the given ID
     */
    @GetMapping("/{id}")
    public Member lookupMemberById(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + id));
        return member;
    }

    /**
     * Creates a new member.
     * 
     * @param member The member to create
     * @return Response indicating success or failure
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            // Register the member
            memberRegistration.register(member);
            
            // Return success response
            return ResponseEntity.ok().build();
            
        } catch (ValidationException e) {
            // Handle unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
            
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
        }
    }

    /**
     * Handles validation exceptions for @Valid annotated parameters.
     * 
     * @param ex The validation exception
     * @return Map of field errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Handles constraint violation exceptions.
     * 
     * @param ex The constraint violation exception
     * @return Map of constraint violations
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        
        return errors;
    }

    /**
     * Checks if a member with the given email already exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    private boolean emailAlreadyExists(String email) {
        try {
            return memberRepository.findByEmail(email) != null;
        } catch (NoResultException e) {
            return false;
        }
    }
}
