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
package org.jboss.as.quickstarts.kitchensink.service;

import lombok.extern.slf4j.Slf4j;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Member entities.
 * Replaces the EJB MemberRegistration with Spring @Service and @Transactional annotations.
 */
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Register a new member
     *
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws Exception If registration fails
     */
    @Transactional
    public Member registerMember(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        if (checkEmailExists(member.getEmail())) {
            log.error("Email {} already exists", member.getEmail());
            throw new Exception("Email already exists");
        }
        
        return memberRepository.save(member);
    }

    /**
     * Get all members ordered by name
     *
     * @return List of all members
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        log.debug("Retrieving all members ordered by name");
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Find a member by ID
     *
     * @param id The ID to search for
     * @return Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(Long id) {
        log.debug("Finding member with ID: {}", id);
        return memberRepository.findById(id);
    }

    /**
     * Find a member by email
     *
     * @param email The email to search for
     * @return Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findMemberByEmail(String email) {
        log.debug("Finding member with email: {}", email);
        return memberRepository.findByEmail(email);
    }

    /**
     * Check if a member with the given email already exists
     *
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        log.debug("Checking if email exists: {}", email);
        return memberRepository.findByEmail(email).isPresent();
    }
}
