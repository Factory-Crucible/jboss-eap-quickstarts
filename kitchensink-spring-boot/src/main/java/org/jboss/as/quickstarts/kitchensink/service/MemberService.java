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

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring service that replaces the original EJB-based MemberRegistration.
 * Provides business logic for member management.
 */
@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    
    /**
     * Constructor injection of dependencies
     * 
     * @param memberRepository The repository for Member entities
     */
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    /**
     * Register a new member
     * 
     * @param member The member to register
     * @return The registered member with ID populated
     * @throws Exception If registration fails, e.g., due to duplicate email
     */
    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        // Check if a member with the same email already exists
        if (emailAlreadyExists(member.getEmail())) {
            throw new Exception("Email already exists: " + member.getEmail());
        }
        
        return memberRepository.save(member);
    }
    
    /**
     * Find all members ordered by name
     * 
     * @return List of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Find a member by ID
     * 
     * @param id The ID to search for
     * @return Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
    
    /**
     * Find a member by email address
     * 
     * @param email The email to search for
     * @return Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Check if a member with the given email already exists
     * 
     * @param email The email to check
     * @return true if a member with the email exists, false otherwise
     */
    private boolean emailAlreadyExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
