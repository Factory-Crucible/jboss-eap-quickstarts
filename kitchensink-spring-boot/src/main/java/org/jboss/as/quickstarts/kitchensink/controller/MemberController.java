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

import java.util.List;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring MVC REST Controller
 * <p>
 * This class produces a RESTful service to read/write the contents of the members table.
 * It replaces the original JAX-RS implementation with Spring MVC.
 */
@RestController
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * Constructor-based injection for {@link MemberService}.
     * <p>
     * Using an explicit constructor avoids relying on Lombok in cases where
     * annotation processing may not be correctly configured during the build.
     *
     * @param memberService the injected member service
     */
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * List all members
     * 
     * @return List of all members
     */
    @GetMapping
    public ResponseEntity<List<Member>> listAllMembers() {
        return ResponseEntity.ok(memberService.findAllOrderedByName());
    }

    /**
     * Lookup a member by id
     * 
     * @param id The id of the member
     * @return The member if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {
        Member member = memberService.findById(id);
        if (member == null) {
            throw new EntityNotFoundException("Member with id " + id + " not found");
        }
        return ResponseEntity.ok(member);
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a response with either 200 ok,
     * or with a map of fields, and related errors.
     * 
     * @param member The member to create
     * @return Response indicating success
     * @throws Exception if registration fails
     */
    @PostMapping
    public ResponseEntity<Void> createMember(@Valid @RequestBody Member member) throws Exception {
        memberService.register(member);
        return ResponseEntity.ok().build();
    }
}
