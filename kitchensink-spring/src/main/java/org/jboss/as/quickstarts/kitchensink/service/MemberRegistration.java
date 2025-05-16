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

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service handling member registration operations.
 * 
 * This class uses Spring's dependency injection and transaction management
 * to handle the registration of new members.
 */
@Service
@Slf4j
public class MemberRegistration {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MemberRegistration(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new member.
     * 
     * This method persists the member to the database and publishes an event
     * to notify other components of the new registration.
     * 
     * @param member The member to register
     * @throws Exception If registration fails
     */
    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        memberRepository.save(member);
        eventPublisher.publishEvent(new MemberRegisteredEvent(member));
    }
    
    /**
     * Event class for member registration.
     * This replaces the CDI event mechanism from the original application.
     */
    public static class MemberRegisteredEvent {
        private final Member member;
        
        public MemberRegisteredEvent(Member member) {
            this.member = member;
        }
        
        public Member getMember() {
            return member;
        }
    }
}
