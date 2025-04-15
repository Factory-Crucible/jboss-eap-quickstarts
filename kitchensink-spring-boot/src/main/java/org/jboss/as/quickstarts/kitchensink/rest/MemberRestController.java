package org.jboss.as.quickstarts.kitchensink.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.jboss.as.quickstarts.kitchensink.service.MemberService.MemberRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Member resources.
 * Migrated from JBoss EAP kitchensink quickstart JAX-RS endpoint.
 */
@RestController
@RequestMapping("/rest/members")
@Validated
@Tag(name = "Member", description = "Member management API")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);
    
    private final MemberService memberService;
    
    @Autowired
    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    @Operation(summary = "Get all members", description = "Returns a list of all members ordered by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of members retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Member.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Member>> listAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = (List<Member>) memberService.findAllOrderedByName();
        return ResponseEntity.ok(members);
    }
    
    @Operation(summary = "Get member by ID", description = "Returns a member by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Member found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Member.class))),
        @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Member> getMemberById(
            @Parameter(description = "ID of the member to retrieve", required = true)
            @PathVariable("id") Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        Member member = memberService.findById(id);
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: " + id);
        }
        return ResponseEntity.ok(member);
    }
    
    @Operation(summary = "Create a new member", description = "Creates a new member and returns the created member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Member created successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Member.class))),
        @ApiResponse(responseCode = "400", description = "Invalid member data provided"),
        @ApiResponse(responseCode = "409", description = "Member with this email already exists")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(
            @Parameter(description = "Member to create", required = true)
            @Valid @RequestBody Member member) {
        log.debug("REST request to create Member: {}", member);
        
        try {
            Member result = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (MemberRegistrationException e) {
            if (e.getMessage().contains("Email already exists")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }
    }
    
    @Operation(summary = "Update an existing member", description = "Updates a member and returns the updated member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Member updated successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Member.class))),
        @ApiResponse(responseCode = "400", description = "Invalid member data provided"),
        @ApiResponse(responseCode = "404", description = "Member not found"),
        @ApiResponse(responseCode = "409", description = "Email already in use by another member")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMember(
            @Parameter(description = "ID of the member to update", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Updated member data", required = true)
            @Valid @RequestBody Member member) {
        log.debug("REST request to update Member with ID: {}", id);
        
        // Check if member exists
        Member existingMember = memberService.findById(id);
        if (existingMember == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: " + id);
        }
        
        // Set the ID to ensure we're updating the correct entity
        member.setId(id);
        
        try {
            Member result = memberService.register(member);
            return ResponseEntity.ok(result);
        } catch (MemberRegistrationException e) {
            if (e.getMessage().contains("Email already exists")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }
    }
    
    @Operation(summary = "Delete a member", description = "Deletes a member by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Member deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "ID of the member to delete", required = true)
            @PathVariable("id") Long id) {
        log.debug("REST request to delete Member with ID: {}", id);
        
        // Check if member exists
        Member existingMember = memberService.findById(id);
        if (existingMember == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: " + id);
        }
        
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
