package com.example.kitchensink.controller;

import com.example.kitchensink.dto.MemberDTO;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing Member resources.
 * This controller provides endpoints for CRUD operations on Member entities
 * and additional functionality like searching and filtering.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    
    private final MemberService memberService;
    
    /**
     * GET /api/members : Get all members.
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        log.debug("REST request to get all Members");
        List<MemberDTO> members = memberService.findAllMembers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /api/members/paged : Get all members with pagination.
     * 
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the page of members in the body
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<MemberDTO>> getPagedMembers(Pageable pageable) {
        log.debug("REST request to get a page of Members");
        Page<MemberDTO> members = memberService.findAllMembers(pageable)
                .map(this::convertToDto);
        
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /api/members/:id : Get the member with the specified id.
     * 
     * @param id the id of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        log.debug("REST request to get Member : {}", id);
        return memberService.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/members : Create a new member.
     * 
     * @param memberDTO the member to create
     * @return the ResponseEntity with status 201 (Created) and with body the new member,
     *         or with status 400 (Bad Request) if the member has invalid fields
     */
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody MemberDTO memberDTO) {
        log.debug("REST request to save Member : {}", memberDTO);
        
        // Ensure the ID is null for creation
        if (memberDTO.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        
        Member member = convertToEntity(memberDTO);
        Member savedMember = memberService.register(member);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedMember.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(convertToDto(savedMember));
    }
    
    /**
     * PUT /api/members/:id : Updates an existing member.
     * 
     * @param id the id of the member to update
     * @param memberDTO the member to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated member,
     *         or with status 400 (Bad Request) if the member has invalid fields,
     *         or with status 404 (Not Found) if the member is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(
            @PathVariable Long id, 
            @Valid @RequestBody MemberDTO memberDTO) {
        
        log.debug("REST request to update Member : {}, {}", id, memberDTO);
        
        if (memberDTO.getId() == null) {
            memberDTO.setId(id);
        } else if (!id.equals(memberDTO.getId())) {
            return ResponseEntity.badRequest().body(null);
        }
        
        return memberService.findById(id)
                .map(existingMember -> {
                    Member member = convertToEntity(memberDTO);
                    member.setId(id);
                    Member updatedMember = memberService.update(member);
                    return ResponseEntity.ok(convertToDto(updatedMember));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * DELETE /api/members/:id : Delete the member with the specified id.
     * 
     * @param id the id of the member to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT) if successful,
     *         or with status 404 (Not Found) if the member is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.debug("REST request to delete Member : {}", id);
        
        return memberService.findById(id)
                .map(member -> {
                    memberService.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/members/by-email/:email : Get the member with the specified email.
     * 
     * @param email the email of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<MemberDTO> getMemberByEmail(@PathVariable String email) {
        log.debug("REST request to get Member by email : {}", email);
        
        return memberService.findByEmail(email)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/members/search : Search members by name.
     * 
     * @param name the name to search for
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembersByName(@RequestParam String name) {
        log.debug("REST request to search Members by name : {}", name);
        
        List<MemberDTO> members = memberService.findByNameContaining(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(members);
    }
    
    /**
     * GET /api/members/search/advanced : Advanced search for members.
     * 
     * @param searchTerm the term to search for in name or email
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the page of members in the body
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<MemberDTO>> advancedSearch(
            @RequestParam String searchTerm,
            Pageable pageable) {
        
        log.debug("REST request for advanced search with term: {} and page: {}", searchTerm, pageable);
        
        Page<MemberDTO> members = memberService.searchMembers(searchTerm, pageable)
                .map(this::convertToDto);
        
        return ResponseEntity.ok(members);
    }
    
    /**
     * Check if an email is available (not already registered).
     * 
     * @param email the email to check
     * @return true if the email is available, false otherwise
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> isEmailAvailable(@RequestParam String email) {
        log.debug("REST request to check if email is available : {}", email);
        
        boolean isAvailable = memberService.isEmailUnique(email);
        return ResponseEntity.ok(isAvailable);
    }
    
    /**
     * Convert a Member entity to a MemberDTO.
     * 
     * @param member the entity to convert
     * @return the converted DTO
     */
    private MemberDTO convertToDto(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
    
    /**
     * Convert a MemberDTO to a Member entity.
     * 
     * @param memberDTO the DTO to convert
     * @return the converted entity
     */
    private Member convertToEntity(MemberDTO memberDTO) {
        return Member.builder()
                .id(memberDTO.getId())
                .name(memberDTO.getName())
                .email(memberDTO.getEmail())
                .phoneNumber(memberDTO.getPhoneNumber())
                .build();
    }
}
