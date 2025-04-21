/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.controller;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST controller for managing Member entities.
 * Provides endpoints for CRUD operations on members.
 */
@RestController
@RequestMapping("/rest/members")
public class MemberController {

    private final Logger log = Logger.getLogger(MemberController.class.getName());

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * GET /rest/members : Get all members.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of members in the body
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        log.info("REST request to get all Members");
        List<Member> members = memberService.findAllOrderedByName();
        return ResponseEntity.ok(members);
    }

    /**
     * GET /rest/members/{id} : Get the member with the specified ID.
     *
     * @param id the ID of the member to retrieve
     * @return the ResponseEntity with status 200 (OK) and the member in the body,
     *         or with status 404 (Not Found) if the member is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        log.info("REST request to get Member with ID: " + id);
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /rest/members : Create a new member.
     *
     * @param member the member to create
     * @return the ResponseEntity with status 201 (Created) and with body the new member,
     *         or with status 400 (Bad Request) if the member has validation errors
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        log.info("REST request to save Member: " + member);
        try {
            Member result = memberService.register(member);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(result.getId())
                    .toUri();
            return ResponseEntity.created(location).body(result);
        } catch (ValidationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("email", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * PUT /rest/members/{id} : Updates an existing member.
     *
     * @param id the ID of the member to update
     * @param member the member to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated member,
     *         or with status 400 (Bad Request) if the member has validation errors,
     *         or with status 404 (Not Found) if the member is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        log.info("REST request to update Member with ID: " + id);
        if (!id.equals(member.getId())) {
            return ResponseEntity.badRequest().body("ID in path does not match ID in request body");
        }
        
        try {
            Member result = memberService.update(member);
            return ResponseEntity.ok(result);
        } catch (ValidationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * DELETE /rest/members/{id} : Delete the member with the specified ID.
     *
     * @param id the ID of the member to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT),
     *         or with status 404 (Not Found) if the member is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.info("REST request to delete Member with ID: " + id);
        try {
            memberService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ValidationException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handle validation exceptions and return appropriate error responses.
     *
     * @param ex the validation exception
     * @return a map of field errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Check if an email already exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    private boolean emailAlreadyExists(String email) {
        return memberService.findByEmail(email).isPresent();
    }
}
