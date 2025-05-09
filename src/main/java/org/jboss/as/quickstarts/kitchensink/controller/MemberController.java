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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST controller for Member entities.
 * This replaces the original JAX-RS based MemberResourceRESTService.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final Logger log;
    private final MemberService memberService;

    /**
     * Constructor for dependency injection.
     * 
     * @param log Logger for logging controller messages
     * @param memberService Service for member operations
     */
    public MemberController(Logger log, MemberService memberService) {
        this.log = log;
        this.memberService = memberService;
    }

    /**
     * GET endpoint to retrieve all members ordered by name.
     * 
     * @return List of all members
     */
    @GetMapping
    public List<Member> getAllMembers() {
        log.info("Getting all members");
        return memberService.getAllMembers();
    }

    /**
     * GET endpoint to retrieve a specific member by ID.
     * 
     * @param id The ID of the member to retrieve
     * @return The member with the specified ID
     * @throws ResponseStatusException if the member is not found
     */
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        log.info("Getting member with ID: " + id);
        return memberService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Member not found with id: " + id));
    }

    /**
     * POST endpoint to create a new member.
     * 
     * @param member The member to create
     * @return ResponseEntity containing the created member and HTTP status
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        try {
            log.info("Creating new member: " + member.getName());
            Member savedMember = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
        } catch (Exception e) {
            log.warning("Error creating member: " + e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
