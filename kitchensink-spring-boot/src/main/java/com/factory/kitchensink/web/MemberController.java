package com.factory.kitchensink.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.service.MemberService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing members.
 * Replaces the JAX-RS endpoint functionality from the JBoss application.
 */
@RestController
@RequestMapping("/rest/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final Validator validator;

    @Autowired
    public MemberController(MemberService memberService, Validator validator) {
        this.memberService = memberService;
        this.validator = validator;
    }

    /**
     * Lists all members.
     * 
     * @return list of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.getAllMembersOrderedByName();
    }

    /**
     * Looks up a member by ID.
     * 
     * @param id the member ID
     * @return the member, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {
        Member member = memberService.findById(id);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    /**
     * Creates a new member.
     * 
     * @param member the member to create
     * @return 200 OK if successful, or an error response
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            // Validate the member manually (in addition to @Valid annotation)
            validateMember(member);
            
            // Register the member
            memberService.register(member);
            
            // Return success response
            return ResponseEntity.ok().build();
        } catch (ConstraintViolationException e) {
            // Handle validation errors
            return createViolationResponse(e.getConstraintViolations());
        } catch (Exception e) {
            // Handle other errors (including duplicate email)
            if (e.getMessage().contains("Email already exists")) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("email", "Email taken");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
            } else {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", e.getMessage());
                return ResponseEntity.badRequest().body(responseObj);
            }
        }
    }

    /**
     * Handles validation errors from @Valid annotation.
     * 
     * @param ex the exception
     * @return map of field errors
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
     * Validates a member using the validator.
     * 
     * @param member the member to validate
     * @throws ConstraintViolationException if validation fails
     */
    private void validateMember(Member member) throws ConstraintViolationException {
        // Create a bean validator and check for issues
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        
        // Email uniqueness is checked in the service layer
    }

    /**
     * Creates a response for validation errors.
     * 
     * @param violations the constraint violations
     * @return the response entity
     */
    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());
        
        Map<String, String> responseObj = new HashMap<>();
        
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        
        return ResponseEntity.badRequest().body(responseObj);
    }
}
