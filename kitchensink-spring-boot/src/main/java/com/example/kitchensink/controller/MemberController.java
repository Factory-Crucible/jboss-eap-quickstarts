package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Member entities.
 * This class replaces the JAX-RS MemberResourceRESTService from the original JBoss application.
 * It uses Spring MVC annotations for REST endpoints and handles validation errors.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    
    private final MemberService memberService;

    /**
     * GET /api/members : Get all members ordered by name.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public List<Member> getAllMembers() {
        log.debug("REST request to get all Members");
        return memberService.findAllMembers();
    }

    /**
     * GET /api/members/{id} : Get the member with the specified ID.
     *
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        try {
            Member member = memberService.findMemberById(id);
            return ResponseEntity.ok(member);
        } catch (EntityNotFoundException e) {
            log.warn("Member not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body,
     *         or with status 400 (Bad Request) if the member has validation errors,
     *         or with status 409 (Conflict) if the email is already in use
     */
    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody Member member) {
        log.debug("REST request to create Member: {}", member);
        
        try {
            Member result = memberService.registerMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ConstraintViolationException e) {
            // Handle bean validation errors
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                String propertyPath = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                errors.put(propertyPath, message);
            }
            log.warn("Validation error for member: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        } catch (ValidationException e) {
            // Handle unique email constraint violation
            Map<String, String> error = new HashMap<>();
            error.put("email", "Email taken");
            log.warn("Email already exists: {}", member.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            // Handle other exceptions
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            log.error("Error creating member", e);
            return ResponseEntity.badRequest().body(error);
        }
    }
}
