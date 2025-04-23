package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Member entities.
 * Provides endpoints for CRUD operations.
 * Migrated from JBoss EAP MemberResourceRESTService to Spring Boot RestController.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * GET /api/members : Get all members
     * Returns a list of all members ordered by name.
     *
     * @return ResponseEntity with the list of members
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * GET /api/members/{id} : Get member by ID
     * Returns the member with the specified ID.
     *
     * @param id the ID of the member to retrieve
     * @return ResponseEntity with the member
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    /**
     * POST /api/members : Create a new member
     * Creates a new member with the provided details.
     *
     * @param member the member to create
     * @return ResponseEntity with the created member and status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        log.debug("REST request to create Member: {}", member);
        
        // Prevent client from providing an ID
        if (member.getId() != null) {
            log.warn("Client attempted to provide an ID for a new member");
            return ResponseEntity.badRequest().build();
        }
        
        Member result = memberService.register(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/members/{id} : Update an existing member
     * Updates the member with the specified ID with the provided details.
     *
     * @param id the ID of the member to update
     * @param member the updated member details
     * @return ResponseEntity with the updated member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id, 
            @Valid @RequestBody Member member) {
        log.debug("REST request to update Member with ID: {}", id);
        
        // Ensure the ID in the path matches the ID in the body, if provided
        if (member.getId() != null && !member.getId().equals(id)) {
            log.warn("ID in path ({}) does not match ID in body ({})", id, member.getId());
            return ResponseEntity.badRequest().build();
        }
        
        // Set the ID to ensure we're updating the correct entity
        member.setId(id);
        
        Member result = memberService.updateMember(id, member);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/members/{id} : Delete a member
     * Deletes the member with the specified ID.
     *
     * @param id the ID of the member to delete
     * @return ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.debug("REST request to delete Member with ID: {}", id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/members/email/{email} : Check if email exists
     * Checks if a member with the specified email exists.
     *
     * @param email the email to check
     * @return ResponseEntity with true if the email exists, false otherwise
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        log.debug("REST request to check if email exists: {}", email);
        boolean exists = memberService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}
