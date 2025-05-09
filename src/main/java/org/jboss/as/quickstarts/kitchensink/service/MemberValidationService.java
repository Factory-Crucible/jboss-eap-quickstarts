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
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service for validating Member entities.
 * This service provides methods to validate members, particularly checking for email uniqueness.
 */
@Service
public class MemberValidationService {

    private final MemberRepository memberRepository;
    private final Logger log;

    /**
     * Constructor for dependency injection.
     * 
     * @param memberRepository Repository for Member entities
     * @param log Logger for logging validation messages
     */
    public MemberValidationService(MemberRepository memberRepository, Logger log) {
        this.memberRepository = memberRepository;
        this.log = log;
    }

    /**
     * Checks if an email is unique (not already used by another member).
     * 
     * @param email The email to check
     * @return true if the email is unique, false otherwise
     */
    public boolean isEmailUnique(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }
    
    /**
     * Validates a Member entity.
     * Currently checks if the email is unique.
     * 
     * @param member The Member entity to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateMember(Member member) {
        log.info("Validating member: " + member.getName());
        
        if (!isEmailUnique(member.getEmail())) {
            log.warning("Email already exists: " + member.getEmail());
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
    }
}
