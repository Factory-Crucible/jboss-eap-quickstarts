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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Unit test for MemberRegistration service.
 * Tests the register method using Mockito to mock dependencies.
 */
@ExtendWith(MockitoExtension.class)
public class MemberRegistrationTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MemberRegistration memberRegistration;

    @BeforeEach
    public void setup() {
        // Create the service with mocked dependencies
        memberRegistration = new MemberRegistration(memberRepository, eventPublisher);
    }

    @Test
    public void testRegister() throws Exception {
        // Create a test member
        Member member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");

        // Set up repository mock behavior
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Call the method being tested
        memberRegistration.register(member);

        // Verify the repository's save method was called with the member
        verify(memberRepository).save(member);

        // Verify the event publisher was called with the member
        verify(eventPublisher).publishEvent(member);
    }

    @Test
    public void testRegisterWithRepositoryException() throws Exception {
        // Create a test member
        Member member = new Member();
        member.setName("Jane Doe");
        member.setEmail("jane.doe@example.com");
        member.setPhoneNumber("9876543210");

        // Set up repository mock to throw an exception
        doThrow(new RuntimeException("Database error")).when(memberRepository).save(any(Member.class));

        // Verify that the exception is propagated
        assertThrows(Exception.class, () -> {
            memberRegistration.register(member);
        });
    }

    @Test
    public void testRegisterWithEventPublisherException() throws Exception {
        // Create a test member
        Member member = new Member();
        member.setName("Alice Smith");
        member.setEmail("alice.smith@example.com");
        member.setPhoneNumber("5551234567");

        // Set up repository mock behavior
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Set up event publisher to throw an exception
        doThrow(new RuntimeException("Event publishing error")).when(eventPublisher).publishEvent(any(Member.class));

        // Verify that the exception is propagated
        assertThrows(Exception.class, () -> {
            memberRegistration.register(member);
        });
    }
}
