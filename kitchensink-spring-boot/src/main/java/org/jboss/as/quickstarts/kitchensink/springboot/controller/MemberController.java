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
package org.jboss.as.quickstarts.kitchensink.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.quickstarts.kitchensink.springboot.model.Member;
import org.jboss.as.quickstarts.kitchensink.springboot.service.MemberService;
import org.jboss.as.quickstarts.kitchensink.springboot.service.MemberService.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for Member resources.
 * This class replaces the original JAX-RS implementation with Spring MVC.
 * It provides endpoints for creating and retrieving member information.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    /**
     * Constructor injection (preferred for immutability and testing).
     *
     * @param memberService service used to manage members
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Lists all members ordered by name.
     *
     * @return List of all members
     */
    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.findAll();
    }

    /**
     * Retrieves a specific member by ID.
     *
     * @param id The member ID
     * @return The member if found
     * @throws ResourceNotFoundException if member not found
     */
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    /**
     * Creates a new member.
     * The request body is validated using Bean Validation.
     *
     * @param member The member to create
     * @return ResponseEntity with the created member and appropriate status code
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        Member createdMember = memberService.register(member);
        log.info("Member registered successfully: {}", createdMember.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    /**
     * Exception handler for validation errors.
     * Maps field validation errors to a map of field names and error messages.
     *
     * @param ex The validation exception
     * @return Map of field names to error messages
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
        log.warn("Validation failed: {}", errors);
        return errors;
    }

    /**
     * Exception handler for duplicate email errors.
     *
     * @param ex The duplicate email exception
     * @return Map with error message
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    public Map<String, String> handleDuplicateEmailException(DuplicateEmailException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("email", "Email taken");
        log.warn("Registration failed: {}", ex.getMessage());
        return error;
    }

    /**
     * Exception thrown when a requested resource is not found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
