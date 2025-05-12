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
package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the {@link MemberRepository}.
 * Uses Spring Boot's testing support for JPA repositories.
 */
@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Tests that a member can be found by email.
     * This tests the findByEmail method in the repository.
     */
    @Test
    public void testFindByEmail() {
        // Given
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");
        entityManager.persist(member);
        entityManager.flush();

        // When
        Optional<Member> found = memberRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
        assertThat(found.get().getPhoneNumber()).isEqualTo("1234567890");
    }

    /**
     * Tests that a member that doesn't exist returns an empty Optional.
     */
    @Test
    public void testFindByEmailNotFound() {
        // Given
        // No members in the database

        // When
        Optional<Member> found = memberRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    /**
     * Tests that all members are returned ordered by name in ascending order.
     * This tests the findAllByOrderByNameAsc method in the repository.
     */
    @Test
    public void testFindAllByOrderByNameAsc() {
        // Given
        Member member1 = new Member();
        member1.setName("John Doe");
        member1.setEmail("john@example.com");
        member1.setPhoneNumber("1234567890");
        entityManager.persist(member1);

        Member member2 = new Member();
        member2.setName("Alice Smith");
        member2.setEmail("alice@example.com");
        member2.setPhoneNumber("0987654321");
        entityManager.persist(member2);

        Member member3 = new Member();
        member3.setName("Bob Johnson");
        member3.setEmail("bob@example.com");
        member3.setPhoneNumber("5555555555");
        entityManager.persist(member3);

        entityManager.flush();

        // When
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Then
        assertThat(members).hasSize(3);
        assertThat(members.get(0).getName()).isEqualTo("Alice Smith");
        assertThat(members.get(1).getName()).isEqualTo("Bob Johnson");
        assertThat(members.get(2).getName()).isEqualTo("John Doe");
    }

    /**
     * Tests that an empty list is returned when there are no members.
     */
    @Test
    public void testFindAllByOrderByNameAscEmpty() {
        // Given
        // No members in the database

        // When
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Then
        assertThat(members).isEmpty();
    }
}
