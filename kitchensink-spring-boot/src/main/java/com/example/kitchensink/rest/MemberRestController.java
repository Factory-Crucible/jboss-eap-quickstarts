package com.example.kitchensink.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

/**
 * REST controller for Member resources.
 * This class replaces the JAX-RS MemberResourceRESTService from the JBoss application.
 * 
 * It provides endpoints for retrieving and creating members.
 */
@RestController
@RequestMapping("/api/members")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);
    
    private final MemberService memberService;
    private final Validator validator;
    
    /**
     * Creates a new MemberRestController with the given dependencies.
     * 
     * @param memberService service for member operations
     * @param validator bean validator for validating member data
     */
    public MemberRestController(MemberService memberService, Validator validator) {
        this.memberService = memberService;
        this.validator = validator;
    }
    
    /**
     * Lists all members ordered by name.
     * 
     * @return list of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.getAllMembers();
    }
    
    /**
     * Gets a member by ID.
     * 
     * @param id the member ID
     * @return the member with the given ID
     * @throws ResponseStatusException if no member is found with the given ID
     */
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Member with ID " + id + " not found"));
    }
    
    /**
     * Creates a new member.
     * This method validates the member data, checks for duplicate emails,
     * and returns appropriate error responses if validation fails.
     * 
     * @param member the member to create
     * @return response with the created member or validation errors
     */
    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody Member member) {
        try {
            // Validate manually to match the original behavior
            Set<ConstraintViolation<Member>> violations = validator.validate(member);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            
            Member savedMember = memberService.register(member);
            return ResponseEntity.ok(savedMember);
        } catch (ConstraintViolationException e) {
            return createViolationResponse(e.getConstraintViolations());
        } catch (IllegalArgumentException e) {
            // This is thrown when email already exists
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
        } catch (Exception e) {
            // Handle general exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseObj);
        }
    }
    
    /**
     * Creates a response containing validation errors.
     * This matches the behavior of the original createViolationResponse method.
     * 
     * @param violations the constraint violations
     * @return response entity with validation errors
     */
    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.info("Validation completed. violations found: {}", violations.size());
        
        Map<String, String> responseObj = new HashMap<>();
        
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        
        return ResponseEntity.badRequest().body(responseObj);
    }
}
