package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

/**
 * REST controller for managing Member entities.
 * Provides endpoints for CRUD operations on members.
 */
@RestController
@RequestMapping("/rest/members")
public class MemberRestController {

    private final MemberService memberService;

    @Autowired
    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /rest/members : Get all members ordered by name.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembersOrderedByName();
        return ResponseEntity.ok(members);
    }

    /**
     * GET /rest/members/{id} : Get the member with the specified ID.
     *
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /rest/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        Member result = memberService.register(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /rest/members/{id} : Update an existing member.
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and the updated member in the body,
     *         or with status 404 (Not Found) if the member doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        try {
            Member updatedMember = memberService.updateMember(id, member);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /rest/members/{id} : Delete a member.
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (No Content),
     *         or with status 404 (Not Found) if the member doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Exception handler for validation errors.
     *
     * @param ex the exception
     * @return a map of field errors
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
     * Exception handler for validation errors.
     *
     * @param ex the exception
     * @return a map with the error message
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidationException(ValidationException ex) {
        Map<String, String> error = new HashMap<>();
        if (ex.getMessage().contains("Email already exists")) {
            error.put("email", "Email taken");
        } else {
            error.put("error", ex.getMessage());
        }
        return error;
    }

    /**
     * Exception handler for general errors.
     *
     * @param ex the exception
     * @return a map with the error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
}
