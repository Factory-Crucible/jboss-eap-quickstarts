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
package org.jboss.as.quickstarts.kitchensink.web;

import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST controller for managing Member entities.
 * This class replaces the original JAX-RS MemberResourceRESTService from JBoss EAP.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final Logger log;
    private final MemberRepository memberRepository;
    private final MemberRegistration registration;

    /**
     * Constructor injection of dependencies.
     * This replaces the CDI @Inject annotations in the original implementation.
     *
     * @param log the logger instance
     * @param memberRepository the repository for member persistence
     * @param registration the service for member registration
     */
    public MemberController(Logger log, MemberRepository memberRepository, 
                          MemberRegistration registration) {
        this.log = log;
        this.memberRepository = memberRepository;
        this.registration = registration;
    }

    /**
     * Lists all members.
     * Replaces the original JAX-RS method with @GET annotation.
     *
     * @return list of all members ordered by name
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Looks up a member by ID.
     * Replaces the original JAX-RS method with @GET and @PathParam annotations.
     *
     * @param id the member ID to look up
     * @return the member if found, or a 404 response if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new member.
     * Replaces the original JAX-RS method with @POST annotation.
     * Uses Spring's @Valid annotation for bean validation.
     *
     * @param member the member to create
     * @return a response indicating success or failure
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            // Check if email already exists
            if (emailAlreadyExists(member.getEmail())) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("email", "Email taken");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
            }

            registration.register(member);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warning("Error creating member: " + e.getMessage());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseObj);
        }
    }

    /**
     * Handles validation exceptions.
     * This replaces the createViolationResponse method in the original implementation.
     *
     * @param ex the validation exception
     * @return a response with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Checks if a member with the given email already exists.
     * This replicates the functionality of the original emailAlreadyExists method.
     *
     * @param email the email to check
     * @return true if the email already exists, false otherwise
     */
    private boolean emailAlreadyExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
