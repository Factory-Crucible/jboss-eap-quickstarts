package com.example.kitchensink.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.MemberNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing Member entities.
 * 
 * This controller provides RESTful endpoints for CRUD operations on Member entities,
 * replacing the JAX-RS implementation in the original JBoss application with
 * Spring MVC annotations and conventions.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * GET /api/members : Get all members ordered by name.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = memberService.findAllOrderedByName();
        return ResponseEntity.ok(members);
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
        log.debug("REST request to get Member : {}", id);
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
    }

    /**
     * POST /api/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body
     * @throws Exception if the member's email already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) throws Exception {
        log.debug("REST request to save Member : {}", member);
        
        // Ensure the ID is null for creation
        if (member.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        
        Member result = memberService.register(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/members/{id} : Update an existing member.
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and the updated member in the body
     * @throws Exception if the member is not found or email already exists
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody Member member) throws Exception {
        log.debug("REST request to update Member : {}", member);
        
        // Ensure the ID in the path matches the ID in the body
        if (member.getId() == null || !member.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        Member result = memberService.update(id, member);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/members/{id} : Delete a member.
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     * @throws Exception if the member is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) throws Exception {
        log.debug("REST request to delete Member : {}", id);
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exception handler for validation errors.
     * 
     * @param ex the exception
     * @return a map of field names to error messages
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
        return errors;
    }

    /**
     * Exception handler for email already exists errors.
     * 
     * @param ex the exception
     * @return a map with the error message
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Email already exists");
        return errors;
    }

    /**
     * Exception handler for member not found errors.
     * 
     * @param ex the exception
     * @return a map with the error message
     */
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleMemberNotFoundException(MemberNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}
