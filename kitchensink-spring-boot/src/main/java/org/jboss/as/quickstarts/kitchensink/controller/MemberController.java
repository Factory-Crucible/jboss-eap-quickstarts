package org.jboss.as.quickstarts.kitchensink.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spring MVC REST Controller for Member resources.
 * Replaces the original JAX-RS MemberResourceRESTService.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final Validator validator;

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    /**
     * Explicit constructor for dependency injection.
     */
    public MemberController(MemberRepository memberRepository,
                             MemberService memberService,
                             Validator validator) {
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.validator = validator;
    }

    /**
     * Lists all members ordered by name
     *
     * @return list of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberRepository.findAllOrderedByName();
    }

    /**
     * Looks up a member by ID
     *
     * @param id the member ID
     * @return the member with the specified ID
     * @throws ResponseStatusException if member not found
     */
    @GetMapping("/{id}")
    public Member lookupMemberById(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + id));
        return member;
    }

    /**
     * Creates a new member
     *
     * @param member the member to create
     * @return response with status and body
     */
    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody @Valid Member member) {
        try {
            // Validate member using bean validation
            validateMember(member);
            
            // Register the member
            Member savedMember = memberService.register(member);
            
            // Return 201 Created with the created member
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            return createViolationResponse(ce.getConstraintViolations());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Email already exists")) {
                // Handle unique constraint violation
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("email", "Email taken");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
            } else {
                // Handle generic exceptions
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
            }
        }
    }

    /**
     * Validates the given Member variable and throws validation exceptions based on the type of error.
     *
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws Exception If member with the same email already exists
     */
    private void validateMember(Member member) throws ConstraintViolationException, Exception {
        // Create a bean validator and check for issues
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(member.getEmail())) {
            throw new Exception("Email already exists: " + member.getEmail());
        }
    }

    /**
     * Creates a response including a map of all violation fields and their messages.
     *
     * @param violations A set of violations that needs to be reported
     * @return ResponseEntity containing all violations
     */
    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.debug("Validation completed. violations found: {}", violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
    }

    /**
     * Checks if a member with the same email address is already registered.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    private boolean emailAlreadyExists(String email) {
        return memberRepository.findByEmail(email) != null;
    }
}
