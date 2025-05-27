package com.example.kitchensink.controller;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for Member resources.
 * This controller provides endpoints for creating, retrieving, and managing members.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Member Management", description = "API endpoints for managing members")
public class MemberController {

    private final MemberService memberService;

    /**
     * Retrieves all members ordered by name.
     *
     * @return a list of all members
     */
    @GetMapping
    @Operation(summary = "Get all members", description = "Retrieves all members ordered by name")
    @ApiResponse(responseCode = "200", description = "Members retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class)))
    public ResponseEntity<List<Member>> listAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = memberService.findAllOrderedByName();
        return ResponseEntity.ok(members);
    }

    /**
     * Retrieves a member by ID.
     *
     * @param id the member ID
     * @return the member
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Retrieves a member by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content)
    })
    public ResponseEntity<Member> getMemberById(
            @Parameter(description = "ID of the member to retrieve", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        Member member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }

    /**
     * Creates a new member.
     *
     * @param member the member to create
     * @return the created member
     */
    @PostMapping
    @Operation(summary = "Register a new member", description = "Creates a new member with validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content)
    })
    public ResponseEntity<Member> createMember(
            @Parameter(description = "Member to be created", required = true, schema = @Schema(implementation = Member.class))
            @Valid @RequestBody Member member) {
        log.debug("REST request to create Member: {}", member);
        
        Member result = memberService.register(member);
        
        // Create location URI for the new resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        
        // Return 201 Created with location header and the created member in the body
        return ResponseEntity.created(location).body(result);
    }
}
