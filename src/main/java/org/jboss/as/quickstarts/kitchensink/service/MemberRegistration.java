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

import java.util.logging.Logger;

/**
 * Service class for Member registration operations.
 * This class replaces the original JBoss EAP EJB implementation with a Spring Service.
 * Uses Spring's @Transactional annotation instead of EJB's container-managed transactions.
 */
@Service
public class MemberRegistration {

    private final Logger log;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor injection of dependencies.
     * This replaces the CDI @Inject annotations in the original implementation.
     *
     * @param log the logger instance
     * @param memberRepository the repository for member persistence
     * @param eventPublisher the Spring event publisher for firing events
     */
    public MemberRegistration(Logger log, MemberRepository memberRepository, 
                            ApplicationEventPublisher eventPublisher) {
        this.log = log;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a new member in the database and fires a member event.
     * This method is transactional, replacing the EJB container-managed transaction.
     *
     * @param member the member to register
     * @throws Exception if there is a problem with registration
     */
    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        memberRepository.save(member);
        eventPublisher.publishEvent(member);
    }
}
