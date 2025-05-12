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
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

/**
 * This class produces a list of all members for use in the UI.
 * This is a Spring component that replaces the CDI-based MemberListProducer from JBoss EAP.
 * It listens for member events and updates the member list accordingly.
 */
@Component
public class MemberListProducer {

    private final MemberRepository memberRepository;
    private final Logger log;
    private List<Member> members;

    /**
     * Constructor injection of dependencies.
     * This replaces the CDI @Inject annotations in the original implementation.
     *
     * @param memberRepository the repository for member persistence
     * @param log the logger instance
     */
    public MemberListProducer(MemberRepository memberRepository, Logger log) {
        this.memberRepository = memberRepository;
        this.log = log;
        this.retrieveAllMembersOrderedByName();
    }

    /**
     * Retrieves all members from the database, ordered by name.
     */
    public void retrieveAllMembersOrderedByName() {
        this.members = memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Returns the current list of members.
     *
     * @return the list of members
     */
    public List<Member> getMembers() {
        return members;
    }

    /**
     * Handles member events (create, update, delete) and refreshes the member list.
     * This replaces the CDI event observer method in the original implementation.
     *
     * @param member the member that triggered the event
     */
    @EventListener
    public void onMemberEvent(Member member) {
        log.info("Received member event for: " + member.getName());
        retrieveAllMembersOrderedByName();
    }
}
