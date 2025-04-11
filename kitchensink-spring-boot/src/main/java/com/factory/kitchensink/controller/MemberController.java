package com.factory.kitchensink.controller;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * REST controller for managing {@link Member} resources.
 * This controller provides endpoints for CRUD operations on members.
 * It follows REST best practices and includes validation, proper HTTP status codes,
 * and content negotiation.
 */
@RestController
@RequestMapping("/rest/members")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Member", description = "Member management API")
public class MemberController {

    private final MemberService memberService;

    /**
     * GET /rest/members : Get all members.
     * Returns a list of all members ordered by name.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all members", description = "Returns a list of all members ordered by name")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved members",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class)))
    public ResponseEntity<List<Member>> getAllMembers() {
        log.debug("REST request to get all Members");
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * GET /rest/members/{id} : Get the member with the specified ID.
     *
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a member by ID", description = "Returns a member as identified by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved member",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<Member> getMemberById(
            @Parameter(description = "ID of the member to retrieve", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: " + id));
    }

    /**
     * POST /rest/members : Register a new member.
     * Creates a new member if the email is not already in use.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and the new member in the body,
     *         or with status 400 (Bad Request) if the member has validation errors,
     *         or with status 409 (Conflict) if the email is already in use
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new member", description = "Creates a new member if the email is not already in use")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "400", description = "Invalid member data provided"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<Member> registerMember(
            @Parameter(description = "Member to register", required = true)
            @Valid @RequestBody Member member) {
        log.debug("REST request to register Member: {}", member);
        try {
            Member result = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * PUT /rest/members/{id} : Update an existing member.
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and the updated member in the body,
     *         or with status 400 (Bad Request) if the member has validation errors,
     *         or with status 404 (Not Found) if the member is not found,
     *         or with status 409 (Conflict) if the email is already in use by another member
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an existing member", description = "Updates an existing member's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "400", description = "Invalid member data provided"),
            @ApiResponse(responseCode = "404", description = "Member not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use by another member")
    })
    public ResponseEntity<Member> updateMember(
            @Parameter(description = "ID of the member to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated member information", required = true)
            @Valid @RequestBody Member member) {
        log.debug("REST request to update Member with ID: {}", id);
        try {
            Member result = memberService.updateMember(id, member);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
        }
    }

    /**
     * DELETE /rest/members/{id} : Delete a member.
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (No Content),
     *         or with status 404 (Not Found) if the member is not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a member", description = "Deletes a member as identified by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "ID of the member to delete", required = true)
            @PathVariable Long id) {
        log.debug("REST request to delete Member with ID: {}", id);
        try {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /rest/members/email/{email} : Get a member by email.
     *
     * @param email the email of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a member by email", description = "Returns a member as identified by their email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved member",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<Member> getMemberByEmail(
            @Parameter(description = "Email of the member to retrieve", required = true)
            @PathVariable @NotNull String email) {
        log.debug("REST request to get Member with email: {}", email);
        return memberService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with email: " + email));
    }

    /**
     * HEAD /rest/members/email/{email} : Check if a member with the given email exists.
     *
     * @param email the email to check
     * @return the ResponseEntity with status 200 (OK) if the email exists,
     *         or with status 404 (Not Found) if the email doesn't exist
     */
    @RequestMapping(value = "/email/{email}", method = RequestMethod.HEAD)
    @Operation(summary = "Check if email exists", description = "Checks if a member with the given email exists")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email exists"),
            @ApiResponse(responseCode = "404", description = "Email does not exist")
    })
    public ResponseEntity<Void> checkEmailExists(
            @Parameter(description = "Email to check", required = true)
            @PathVariable @NotNull String email) {
        log.debug("REST request to check if email exists: {}", email);
        return memberService.findByEmail(email)
                .map(m -> ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
