package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.example.kitchensink.service.MemberService.EmailAlreadyExistsException;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Member resources.
 * Replaces the original JBoss kitchensink MemberResourceRESTService JAX-RS resource.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    
    private final MemberService memberService;

    /**
     * Constructor injection of dependencies
     * 
     * @param memberService The service for Member operations
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * List all members
     * 
     * @return List of all members ordered by name
     */
    @GetMapping
    public ResponseEntity<List<Member>> listAllMembers() {
        List<Member> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }

    /**
     * Get a member by ID
     * 
     * @param id The ID of the member to retrieve
     * @return The member if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new member
     * 
     * @param member The member to create
     * @return The created member with status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        Member savedMember = memberService.register(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
    }

    /**
     * Handle validation errors
     * 
     * @param ex The validation exception
     * @return Map of field errors with status 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.info("Validation errors: {}", errors);
        return errors;
    }

    /**
     * Handle email already exists exception
     * 
     * @param ex The email already exists exception
     * @return Error message with status 409 (Conflict)
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("email", "Email taken");
        log.info("Email already exists: {}", ex.getMessage());
        return error;
    }

    /**
     * Handle generic exceptions
     * 
     * @param ex The exception
     * @return Error message with status 400 (Bad Request)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleGenericExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Unexpected error", ex);
        return error;
    }
}
