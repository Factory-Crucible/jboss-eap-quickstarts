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
package org.jboss.as.quickstarts.kitchensink.springboot.service;

import java.util.List;
import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.springboot.model.Member;
import org.jboss.as.quickstarts.kitchensink.springboot.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing Member entities.
 * This class replaces the original EJB implementation with a Spring Service.
 * It provides methods for registering, finding, and validating members.
 */
@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;

    /**
     * Constructor injection for {@link MemberRepository}.
     *
     * @param memberRepository repository used by this service
     */
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Register a new member.
     * This method is transactional to ensure data consistency.
     *
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws DuplicateEmailException If a member with the same email already exists
     */
    @Transactional
    public Member register(Member member) throws DuplicateEmailException {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            log.warn("Email {} already exists. Registration failed.", member.getEmail());
            throw new DuplicateEmailException("Email already exists: " + member.getEmail());
        }
        
        return memberRepository.save(member);
    }

    /**
     * Find all members ordered by name.
     *
     * @return List of all members
     */
    public List<Member> findAll() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Find a member by ID.
     *
     * @param id The member ID
     * @return Optional containing the member if found
     */
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Find a member by email address.
     *
     * @param email The email to search for
     * @return Optional containing the member if found
     */
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Check if a member with the given email already exists.
     *
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * Exception thrown when attempting to register a member with an email that already exists.
     */
    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String message) {
            super(message);
        }
    }
}
