package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing {@link Member} entities.
 * This controller exposes endpoints for CRUD operations on members.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberRestController {

    private final MemberService memberService;

    /**
     * GET /api/members : Get all members.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = memberService.findAllMembersOrderedByName();
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
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        return memberService.findMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body,
     *         or with status 400 (Bad Request) if the member is invalid or the email already exists
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        log.debug("REST request to create Member: {}", member);
        
        try {
            Member result = memberService.createMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            log.error("Email already exists: {}", member.getEmail());
            throw e;
        } catch (ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * PUT /api/members/{id} : Update an existing member.
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and the updated member in the body,
     *         or with status 400 (Bad Request) if the member is invalid,
     *         or with status 404 (Not Found) if the member is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        log.debug("REST request to update Member with ID: {}", id);
        
        try {
            Member result = memberService.updateMember(id, member);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Error updating member: {}", e.getMessage());
            throw e;
        } catch (ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * DELETE /api/members/{id} : Delete the member with the specified ID.
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (No Content),
     *         or with status 404 (Not Found) if the member is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.debug("REST request to delete Member with ID: {}", id);
        
        try {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Member not found with ID: {}", id);
            throw e;
        }
    }

    /**
     * Exception handler for validation errors.
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
        return errors;
    }

    /**
     * Exception handler for illegal argument exceptions.
     *
     * @param ex the exception
     * @return a map with the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    /**
     * Exception handler for validation exceptions.
     *
     * @param ex the exception
     * @return a map with the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidationException(ValidationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
}
