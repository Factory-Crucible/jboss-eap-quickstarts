package com.example.kitchensink.controller;

import com.example.kitchensink.exception.DuplicateResourceException;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Member entities.
 * 
 * This controller provides REST endpoints for creating, retrieving,
 * and listing Member entities.
 */
@Slf4j
@RestController
@RequestMapping("/api/members")
@Validated
public class MemberRestController {

    private final MemberService memberService;

    /**
     * Constructs a new MemberRestController with the required dependencies.
     * 
     * @param memberService Service for Member operations
     */
    @Autowired
    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Retrieves all members ordered by name.
     * 
     * @return A list of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        log.debug("REST request to get all Members");
        return memberService.findAllOrderedByName();
    }

    /**
     * Retrieves a member by their ID.
     * 
     * @param id The ID of the member to retrieve
     * @return The member with the given ID
     * @throws ResourceNotFoundException If no member with the given ID exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        log.debug("REST request to get Member by ID: {}", id);
        try {
            Member member = memberService.findById(id);
            return ResponseEntity.ok(member);
        } catch (ResourceNotFoundException e) {
            log.error("Member not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a member by their email address.
     * 
     * @param email The email address to search for
     * @return The member with the given email
     * @throws ResourceNotFoundException If no member with the given email exists
     */
    @GetMapping("/by-email")
    public ResponseEntity<Member> getMemberByEmail(
            @RequestParam @NotBlank @Email String email) {
        log.debug("REST request to get Member by email: {}", email);
        try {
            Member member = memberService.findByEmail(email);
            return ResponseEntity.ok(member);
        } catch (ResourceNotFoundException e) {
            log.error("Member not found with email: {}", email, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new member.
     * 
     * @param member The member to create
     * @return The created member
     * @throws DuplicateResourceException If a member with the same email already exists
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        log.debug("REST request to save Member: {}", member);
        try {
            Member result = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (DuplicateResourceException e) {
            log.error("Failed to create member due to duplicate email", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("email", "Email already exists: " + member.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * Checks if a member with the given email exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(
            @RequestParam @NotBlank @Email String email) {
        log.debug("REST request to check if email exists: {}", email);
        boolean exists = memberService.emailExists(email);
        Map<String, Boolean> response = Map.of("exists", exists);
        return ResponseEntity.ok(response);
    }
}
