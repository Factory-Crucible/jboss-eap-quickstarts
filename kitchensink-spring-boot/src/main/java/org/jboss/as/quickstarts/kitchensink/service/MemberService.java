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

import java.util.List;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This service handles member registration and lookup operations.
 * It replaces the original EJB-based MemberRegistration with a Spring Service.
 */
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    /**
     * Explicit constructor for dependency injection.
     *
     * @param memberRepository  the repository to access members
     * @param eventPublisher    publisher for domain events
     */
    @Autowired
    public MemberService(MemberRepository memberRepository,
                         ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new member in the database
     *
     * @param member The member to register
     * @throws Exception if registration fails
     */
    public void register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        
        // Check if a member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new EntityExistsException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        memberRepository.save(member);
        
        // Publish an event to notify other components
        eventPublisher.publishEvent(member);
    }
    
    /**
     * Find a member by ID
     *
     * @param id The member ID
     * @return The member, or null if not found
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }
    
    /**
     * Find a member by email address
     *
     * @param email The email address
     * @return The member, or null if not found
     */
    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Get all members ordered by name
     *
     * @return List of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }
}
