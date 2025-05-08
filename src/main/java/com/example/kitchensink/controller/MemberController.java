package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link Member} entities.
 * This controller exposes endpoints for CRUD operations on members.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final Logger logger = LoggerFactory.getLogger(MemberController.class);

    /**
     * Constructor for dependency injection.
     *
     * @param memberService the service for member operations
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /api/members : Get all members.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        logger.debug("REST request to get all Members");
        List<Member> members = memberService.findAll();
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
        logger.debug("REST request to get Member with ID: {}", id);
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        logger.debug("REST request to save Member: {}", member);
        
        // Ensure a new member doesn't have an ID
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
     * @return the ResponseEntity with status 200 (OK) and the updated member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody Member member) {
        logger.debug("REST request to update Member with ID: {}", id);
        
        // Ensure the IDs match
        if (member.getId() != null && !member.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        Member result = memberService.update(id, member);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/members/{id} : Delete the member with the specified ID.
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (No Content) if successful,
     *         or with status 404 (Not Found) if the member is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        logger.debug("REST request to delete Member with ID: {}", id);
        boolean deleted = memberService.delete(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
