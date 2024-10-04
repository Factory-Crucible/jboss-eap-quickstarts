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
package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.logging.Logger;

// The @Service annotation is used to denote a Spring service component
@Service
public class MemberRegistrationService {

    private final Logger log = Logger.getLogger(MemberRegistrationService.class.getName());
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Constructor-based dependency injection
    public MemberRegistrationService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    // The @Transactional annotation ensures that the method runs within a transaction
    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        memberRepository.save(member);
        eventPublisher.publishEvent(member);
    }
}