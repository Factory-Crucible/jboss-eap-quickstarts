package com.example.kitchensink.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.exception.MemberNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;

import jakarta.validation.Valid;

/**
 * REST controller for managing Member entities.
 * Provides endpoints for CRUD operations on members.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final Logger log = Logger.getLogger(MemberController.class.getName());
    
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    /**
     * GET /api/members : Get all members ordered by name
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        log.info("REST request to get all Members");
        List<Member> members = memberService.getAllMembersOrderedByName();
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /api/members/{id} : Get the member with the specified ID
     *
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        log.info("REST request to get Member : " + id);
        Member member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }
    
    /**
     * POST /api/members : Create a new member
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        log.info("REST request to save Member : " + member.getName());
        
        if (member.getId() != null) {
            return ResponseEntity.badRequest()
                    .header("X-memberApp-error", "A new member cannot already have an ID")
                    .build();
        }
        
        Member result = memberService.register(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * PUT /api/members/{id} : Update an existing member
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and the updated member in the body,
     *         or with status 400 (Bad Request) if the ID is invalid
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        log.info("REST request to update Member : " + id);
        
        if (member.getId() == null) {
            member.setId(id);
        } else if (!member.getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .header("X-memberApp-error", "ID in path does not match ID in body")
                    .build();
        }
        
        Member result = memberService.updateMember(id, member);
        return ResponseEntity.ok(result);
    }
    
    /**
     * DELETE /api/members/{id} : Delete the member with the specified ID
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.info("REST request to delete Member : " + id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Handle validation exceptions
     *
     * @param ex the MethodArgumentNotValidException
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
     * Handle EmailAlreadyExistsException
     *
     * @param ex the EmailAlreadyExistsException
     * @return a map with the error message
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("email", "Email already exists");
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }
    
    /**
     * Handle MemberNotFoundException
     *
     * @param ex the MemberNotFoundException
     * @return a map with the error message
     */
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleMemberNotFoundException(MemberNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }
    
    /**
     * Handle generic exceptions
     *
     * @param ex the Exception
     * @return a map with the error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }
}
