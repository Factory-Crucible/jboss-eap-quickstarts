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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import jakarta.persistence.EntityExistsException;

/**
 * Unit tests for the MemberService class.
 * 
 * This test class uses Mockito to mock dependencies and focuses on testing
 * the service logic in isolation from other components.
 */
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    public void setUp() {
        // Create a test member for use in tests
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("Test User");
        testMember.setEmail("test@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    /**
     * Test registering a valid member.
     * Should save the member and publish an event.
     */
    @Test
    public void testRegisterValidMember() throws Exception {
        // Given
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(null);
        
        // When
        memberService.register(testMember);
        
        // Then
        verify(memberRepository).save(testMember);
        verify(eventPublisher).publishEvent(testMember);
    }

    /**
     * Test registering a member with a duplicate email.
     * Should throw EntityExistsException.
     */
    @Test
    public void testRegisterDuplicateEmail() {
        // Given
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(testMember);
        
        // When/Then
        EntityExistsException exception = assertThrows(
            EntityExistsException.class, 
            () -> memberService.register(testMember)
        );
        
        // Verify the exception message contains the email
        assertEquals("Email already exists: " + testMember.getEmail(), exception.getMessage());
        
        // Verify the member was not saved and no event was published
        verify(memberRepository, never()).save(any(Member.class));
        verify(eventPublisher, never()).publishEvent(any(Member.class));
    }

    /**
     * Test finding a member by ID when the member exists.
     * Should return the member.
     */
    @Test
    public void testFindByIdExistingMember() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        
        // When
        Member result = memberService.findById(1L);
        
        // Then
        assertSame(testMember, result);
    }

    /**
     * Test finding a member by ID when the member does not exist.
     * Should return null.
     */
    @Test
    public void testFindByIdNonExistingMember() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Member result = memberService.findById(999L);
        
        // Then
        assertNull(result);
    }

    /**
     * Test finding a member by email.
     * Should delegate to the repository.
     */
    @Test
    public void testFindByEmail() {
        // Given
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(testMember);
        
        // When
        Member result = memberService.findByEmail(testMember.getEmail());
        
        // Then
        assertSame(testMember, result);
        verify(memberRepository).findByEmail(testMember.getEmail());
    }

    /**
     * Test finding all members ordered by name.
     * Should delegate to the repository.
     */
    @Test
    public void testFindAllOrderedByName() {
        // Given
        Member member1 = new Member();
        member1.setName("Alice");
        
        Member member2 = new Member();
        member2.setName("Bob");
        
        List<Member> expectedMembers = Arrays.asList(member1, member2);
        when(memberRepository.findAllOrderedByName()).thenReturn(expectedMembers);
        
        // When
        List<Member> result = memberService.findAllOrderedByName();
        
        // Then
        assertSame(expectedMembers, result);
        verify(memberRepository).findAllOrderedByName();
    }
}
