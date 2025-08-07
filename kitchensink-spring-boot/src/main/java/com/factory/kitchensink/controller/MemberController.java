package com.factory.kitchensink.controller;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
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
 * REST controller for managing Member entities.
 * 
 * This is a migration of the original JBoss EAP kitchensink MemberResourceRESTService JAX-RS resource.
 * 
 * Migration mapping:
 * - Original: JAX-RS @Path("/members") with @RequestScoped
 * - Spring Boot: @RestController with @RequestMapping("/api/members")
 * 
 * - Original: JAX-RS @GET, @POST annotations
 * - Spring Boot: Spring MVC @GetMapping, @PostMapping annotations
 * 
 * - Original: JAX-RS @PathParam
 * - Spring Boot: Spring MVC @PathVariable
 * 
 * - Original: JAX-RS Response.ResponseBuilder for responses
 * - Spring Boot: ResponseEntity for typed responses
 * 
 * - Original: Manual validation handling
 * - Spring Boot: @Valid annotation with exception handlers
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    
    private final MemberService memberService;
    
    /**
     * GET /api/members : Get all members.
     * 
     * Replaces the original listAllMembers method in MemberResourceRESTService.
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        logger.debug("REST request to get all Members");
        List<Member> members = memberService.findAllMembers();
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /api/members/{id} : Get the member with the specified ID.
     * 
     * Replaces the original lookupMemberById method in MemberResourceRESTService.
     * 
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        logger.debug("REST request to get Member : {}", id);
        Member member = memberService.findById(id);
        if (member == null) {
            logger.warn("Member with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }
    
    /**
     * POST /api/members : Create a new member.
     * 
     * Replaces the original createMember method in MemberResourceRESTService.
     * 
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        logger.debug("REST request to save Member : {}", member);
        
        // The @Valid annotation will trigger validation
        // If validation fails, it will be handled by handleValidationExceptions
        
        try {
            Member result = memberService.registerMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ValidationException e) {
            // This handles the case where email already exists
            logger.warn("Validation error when creating member: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Exception handler for validation errors.
     * 
     * Replaces the createViolationResponse method in MemberResourceRESTService.
     * 
     * @param ex the exception
     * @return a map of field errors
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
        logger.warn("Validation errors: {}", errors);
        return errors;
    }
    
    /**
     * Exception handler for validation exceptions thrown from the service layer.
     * 
     * Handles cases like duplicate email addresses.
     * 
     * @param ex the exception
     * @return a map with the error message
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidationException(ValidationException ex) {
        Map<String, String> error = new HashMap<>();
        if (ex.getMessage().contains("Email")) {
            error.put("email", "Email address already exists");
        } else {
            error.put("error", ex.getMessage());
        }
        logger.warn("Validation exception: {}", ex.getMessage());
        return error;
    }
}
