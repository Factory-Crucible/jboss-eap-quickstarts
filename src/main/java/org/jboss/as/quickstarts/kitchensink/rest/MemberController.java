package org.jboss.as.quickstarts.kitchensink.rest;

import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Member entities.
 * Provides endpoints for CRUD operations on members.
 */
@RestController
@RequestMapping("/rest/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * Constructor injection for dependencies
     *
     * @param memberService the service for member operations
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /rest/members : Get all members
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.findAllOrderedByName();
        return ResponseEntity.ok(members);
    }

    /**
     * GET /rest/members/{id} : Get the member with the specified ID
     *
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in body, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        try {
            Member member = memberService.findById(id);
            return ResponseEntity.ok(member);
        } catch (MemberService.ServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /rest/members : Create a new member
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in body
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            Member result = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (MemberService.ServiceException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("email", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * PUT /rest/members/{id} : Update an existing member
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and the updated member in body
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        try {
            Member result = memberService.update(id, member);
            return ResponseEntity.ok(result);
        } catch (MemberService.ServiceException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("already exists")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    /**
     * DELETE /rest/members/{id} : Delete the member with the specified ID
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        try {
            memberService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (MemberService.ServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Exception handler for validation errors
     *
     * @param ex the exception
     * @return the ResponseEntity with status 400 (Bad Request) and validation errors in body
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
}
