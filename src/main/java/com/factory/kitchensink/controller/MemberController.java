package com.factory.kitchensink.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.factory.kitchensink.dto.CreateMemberDTO;
import com.factory.kitchensink.dto.MemberDTO;
import com.factory.kitchensink.service.MemberService;

import jakarta.validation.Valid;

/**
 * REST controller for managing members.
 * Provides endpoints for member-related operations.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {
    
    private final MemberService memberService;
    
    /**
     * Constructor-based dependency injection.
     * 
     * @param memberService service for member-related operations
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
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = memberService.findAllMembers();
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /api/members/{id} : Get a member by ID.
     * 
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }
    
    /**
     * GET /api/members/email/{email} : Get a member by email.
     * 
     * @param email the email of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberDTO> getMemberByEmail(@PathVariable String email) {
        MemberDTO member = memberService.findByEmail(email);
        return ResponseEntity.ok(member);
    }
    
    /**
     * POST /api/members : Register a new member.
     * 
     * @param memberDTO the member to register
     * @return the ResponseEntity with status 201 (Created) and the new member in the body
     */
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody CreateMemberDTO memberDTO) {
        MemberDTO createdMember = memberService.register(memberDTO);
        return ResponseEntity
                .created(URI.create("/api/members/" + createdMember.getId()))
                .body(createdMember);
    }
}
