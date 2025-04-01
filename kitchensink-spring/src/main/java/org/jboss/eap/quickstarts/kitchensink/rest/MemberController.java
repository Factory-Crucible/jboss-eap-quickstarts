package org.jboss.eap.quickstarts.kitchensink.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.kitchensink.model.Member;
import org.jboss.eap.quickstarts.kitchensink.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing Member resources.
 * This class handles HTTP requests for the Member entity.
 * 
 * Migrated from the JBoss EAP Kitchensink application's MemberResourceRESTService class.
 * The JAX-RS annotations have been replaced with Spring MVC annotations.
 */
@RestController
@RequestMapping("/api/members")
@Validated
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Retrieves all members ordered by name.
     * 
     * @return List of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.getAllMembersOrderedByName();
    }

    /**
     * Retrieves a member by ID.
     * 
     * @param id The ID of the member to retrieve
     * @return The member with the given ID
     * @throws ResponseStatusException if the member is not found
     */
    @GetMapping("/{id}")
    public Member lookupMemberById(@PathVariable("id") Long id) {
        return memberService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Member with id " + id + " not found"));
    }

    /**
     * Retrieves a member by email.
     * 
     * @param email The email of the member to retrieve
     * @return The member with the given email
     * @throws ResponseStatusException if the member is not found
     */
    @GetMapping("/email/{email}")
    public Member lookupMemberByEmail(@PathVariable("email") String email) {
        return memberService.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Member with email " + email + " not found"));
    }

    /**
     * Creates a new member.
     * 
     * @param member The member to create
     * @return ResponseEntity with status code and optional error message
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody @NotNull Member member) {
        try {
            Member createdMember = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (Exception e) {
            log.error("Error creating member", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error creating member: " + e.getMessage());
        }
    }

    /**
     * Updates an existing member.
     * 
     * @param id The ID of the member to update
     * @param member The updated member details
     * @return ResponseEntity with status code and optional error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable("id") Long id, 
                                         @Valid @RequestBody @NotNull Member member) {
        try {
            Member updatedMember = memberService.updateMember(id, member);
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            log.error("Error updating member", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error updating member: " + e.getMessage());
        }
    }

    /**
     * Deletes a member by ID.
     * 
     * @param id The ID of the member to delete
     * @return ResponseEntity with status code and optional error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable("id") Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting member", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting member: " + e.getMessage());
        }
    }

    /**
     * Checks if a member with the given email exists.
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    @GetMapping("/exists/{email}")
    public boolean checkEmailExists(@PathVariable("email") String email) {
        return memberService.emailExists(email);
    }
}
