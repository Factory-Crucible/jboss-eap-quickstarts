package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Member operations.
 * This class replaces the MemberResourceRESTService from the original JBoss application.
 * It provides endpoints for creating, retrieving, updating, and deleting members.
 */
@RestController
@RequestMapping("/api/members")
@Slf4j
@RequiredArgsConstructor
public class MemberRestController {
    
    private final MemberService memberService;
    
    /**
     * Retrieves all members ordered by name.
     * 
     * @return a list of all members
     */
    @GetMapping
    public List<Member> getAllMembers() {
        log.debug("REST request to get all Members");
        return memberService.findAllOrderedByName();
    }
    
    /**
     * Retrieves a specific member by ID.
     * 
     * @param id the ID of the member to retrieve
     * @return the member, or a 404 status if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        try {
            Member member = memberService.findById(id);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            log.error("Member not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Creates a new member.
     * 
     * @param member the member to create
     * @return the created member with a 201 status, or an error response
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        log.debug("REST request to create Member: {}", member);
        try {
            // Ensure the ID is null for creation
            member.setId(null);
            Member savedMember = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
        } catch (IllegalArgumentException e) {
            log.error("Error creating member: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            if (e.getMessage().contains("Email already exists")) {
                errorResponse.put("email", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else {
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Unexpected error creating member", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Updates an existing member.
     * 
     * @param id the ID of the member to update
     * @param member the updated member data
     * @return the updated member, or an error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        log.debug("REST request to update Member with ID: {}", id);
        try {
            // Ensure the ID in the path matches the ID in the body
            member.setId(id);
            Member updatedMember = memberService.update(member);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            log.error("Error updating member: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            if (e.getMessage().contains("Email already exists")) {
                errorResponse.put("email", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else if (e.getMessage().contains("Cannot update non-existent member")) {
                errorResponse.put("error", "Member not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else {
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Unexpected error updating member", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the ID of the member to delete
     * @return a 204 status if successful, or an error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        log.debug("REST request to delete Member with ID: {}", id);
        try {
            memberService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting member: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Member not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error deleting member", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
