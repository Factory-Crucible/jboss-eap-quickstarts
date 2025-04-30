package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing Member resources.
 * This controller replaces the functionality of MemberResourceRESTService.java
 * from the original JBoss application.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /api/members : Get all members ordered by name.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Member>> getAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = memberService.findAllOrderedByName();
        return ResponseEntity.ok(members);
    }

    /**
     * GET /api/members/{id} : Get the member with the specified id.
     *
     * @param id the id of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the member,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        log.debug("REST request to get Member : {}", id);
        Member member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }

    /**
     * POST /api/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and with body the new member,
     *         or with status 400 (Bad Request) if the member has invalid fields,
     *         or with status 409 (Conflict) if the email is already in use
     */
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        log.debug("REST request to save Member : {}", member);
        
        // Ensure the ID is not set - we're creating a new entity
        if (member.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        
        Member result = memberService.register(member);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(result.getId())
            .toUri();
            
        return ResponseEntity.created(location).body(result);
    }

    /**
     * GET /api/members/email/{email} : Get the member with the specified email.
     *
     * @param email the email of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the member,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Member> getMemberByEmail(@PathVariable String email) {
        log.debug("REST request to get Member by email : {}", email);
        Member member = memberService.findByEmail(email);
        return ResponseEntity.ok(member);
    }

    /**
     * HEAD /api/members/email/{email} : Check if a member with the specified email exists.
     *
     * @param email the email to check
     * @return the ResponseEntity with status 200 (OK) if the email exists,
     *         or with status 404 (Not Found) if the email does not exist
     */
    @RequestMapping(value = "/email/{email}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkEmailExists(@PathVariable String email) {
        log.debug("REST request to check if email exists : {}", email);
        return memberService.emailExists(email) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }
}
