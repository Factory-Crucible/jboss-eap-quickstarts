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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for Member registration and management.
 * This replaces the original EJB-based MemberRegistration class.
 */
@Service
public class MemberService {

    private final Logger log;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberValidationService validationService;

    /**
     * Constructor for dependency injection.
     * 
     * @param log Logger for logging registration messages
     * @param memberRepository Repository for Member entities
     * @param eventPublisher Publisher for member registration events
     * @param validationService Service for validating members
     */
    public MemberService(Logger log, 
                        MemberRepository memberRepository, 
                        ApplicationEventPublisher eventPublisher,
                        MemberValidationService validationService) {
        this.log = log;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
        this.validationService = validationService;
    }

    /**
     * Registers a new member.
     * Validates the member, persists it to the database, and publishes an event.
     * 
     * @param member The Member entity to register
     * @return The registered Member with its generated ID
     * @throws Exception if registration fails
     */
    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        
        // Validate the member (check for unique email)
        validationService.validateMember(member);
        
        // Save the member to the database
        Member savedMember = memberRepository.save(member);
        
        // Publish an event that a new member has been registered
        eventPublisher.publishEvent(savedMember);
        
        return savedMember;
    }
    
    /**
     * Retrieves all members ordered by name.
     * 
     * @return List of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Finds a member by ID.
     * 
     * @param id The ID of the member to find
     * @return An Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
    
    /**
     * Finds a member by email.
     * 
     * @param email The email of the member to find
     * @return An Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
