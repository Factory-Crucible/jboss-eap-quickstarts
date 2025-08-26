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

import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring MVC REST Controller for Member resources.
 * Replaces the original JAX-RS based REST service.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    /**
     * Constructor injection of dependencies
     * 
     * @param memberService The service for Member operations
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * List all members ordered by name
     * 
     * @return List of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        log.debug("REST request to get all Members");
        return memberService.findAllOrderedByName();
    }

    /**
     * Get a specific member by ID
     * 
     * @param id The ID of the member to retrieve
     * @return The member if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable("id") Long id) {
        log.debug("REST request to get Member with ID: {}", id);
        return memberService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new member
     * 
     * @param member The member to create
     * @return The created member or error details
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        log.debug("REST request to save Member: {}", member);
        
        try {
            // Check if email already exists
            if (memberService.findByEmail(member.getEmail()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            
            // Register the new member
            Member result = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (Exception e) {
            log.error("Error creating member", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Handle validation errors
     * 
     * @param ex The validation exception
     * @return Map of field errors
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
        log.debug("Validation errors: {}", errors);
        return errors;
    }
}
