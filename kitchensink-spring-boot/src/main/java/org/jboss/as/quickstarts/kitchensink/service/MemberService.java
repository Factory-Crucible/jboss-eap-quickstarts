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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class for managing Member entities.
 * Provides methods for registering, finding, updating, and deleting members.
 */
@Service
public class MemberService {

    private final Logger log = Logger.getLogger(MemberService.class.getName());
    
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new member.
     * 
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws ValidationException if a member with the same email already exists
     */
    @Transactional
    public Member register(Member member) throws ValidationException {
        log.info("Registering " + member.getName());
        
        // Check if a member with the same email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new ValidationException("Email already exists: " + member.getEmail());
        }
        
        // Save the member
        Member savedMember = memberRepository.save(member);
        
        // Publish an event to notify other components
        eventPublisher.publishEvent(new MemberRegistrationEvent(savedMember));
        
        return savedMember;
    }

    /**
     * Finds all members ordered by name.
     * 
     * @return a list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Finds a member by ID.
     * 
     * @param id the ID of the member to find
     * @return an Optional containing the member if found, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Finds a member by email.
     * 
     * @param email the email of the member to find
     * @return an Optional containing the member if found, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Updates an existing member.
     * 
     * @param member the member to update
     * @return the updated member
     * @throws ValidationException if the member does not exist or if trying to change
     *                             the email to one that already exists
     */
    @Transactional
    public Member update(Member member) throws ValidationException {
        if (member.getId() == null) {
            throw new ValidationException("Member ID cannot be null for update operation");
        }
        
        // Check if the member exists
        if (!memberRepository.existsById(member.getId())) {
            throw new ValidationException("Member with ID " + member.getId() + " does not exist");
        }
        
        // Check if trying to change email to one that already exists
        Optional<Member> existingMemberWithEmail = memberRepository.findByEmail(member.getEmail());
        if (existingMemberWithEmail.isPresent() && !existingMemberWithEmail.get().getId().equals(member.getId())) {
            throw new ValidationException("Email already exists: " + member.getEmail());
        }
        
        // Save the updated member
        Member updatedMember = memberRepository.save(member);
        
        // Publish an event to notify other components
        eventPublisher.publishEvent(new MemberRegistrationEvent(updatedMember));
        
        return updatedMember;
    }

    /**
     * Deletes a member by ID.
     * 
     * @param id the ID of the member to delete
     * @throws ValidationException if the member does not exist
     */
    @Transactional
    public void delete(Long id) throws ValidationException {
        if (!memberRepository.existsById(id)) {
            throw new ValidationException("Member with ID " + id + " does not exist");
        }
        
        memberRepository.deleteById(id);
    }

    /**
     * Event class for member registration events.
     */
    public static class MemberRegistrationEvent {
        private final Member member;

        public MemberRegistrationEvent(Member member) {
            this.member = member;
        }

        public Member getMember() {
            return member;
        }
    }
}
