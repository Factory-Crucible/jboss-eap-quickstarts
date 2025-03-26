package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the {@link MemberRepository}.
 * These tests verify that the repository methods work as expected.
 */
@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member john;
    private Member jane;
    private Member bob;

    @BeforeEach
    public void setUp() {
        // Create test members
        john = Member.builder()
                .name("John Smith")
                .email("john.smith@mailinator.com")
                .phoneNumber("2125551212")
                .build();

        jane = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@mailinator.com")
                .phoneNumber("2125552323")
                .build();

        bob = Member.builder()
                .name("Bob Johnson")
                .email("bob.johnson@mailinator.com")
                .phoneNumber("2125553434")
                .build();

        // Persist test members
        entityManager.persist(john);
        entityManager.persist(jane);
        entityManager.persist(bob);
        entityManager.flush();
    }

    @Test
    public void testFindByEmail() {
        // When
        Optional<Member> foundMember = memberRepository.findByEmail("john.smith@mailinator.com");

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("John Smith");
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("2125551212");
    }

    @Test
    public void testFindByEmail_NotFound() {
        // When
        Optional<Member> foundMember = memberRepository.findByEmail("nonexistent@mailinator.com");

        // Then
        assertThat(foundMember).isEmpty();
    }

    @Test
    public void testExistsByEmail() {
        // When
        boolean exists = memberRepository.existsByEmail("jane.doe@mailinator.com");
        boolean notExists = memberRepository.existsByEmail("nonexistent@mailinator.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testFindAllOrderedByName() {
        // When
        List<Member> members = memberRepository.findAllOrderedByName();

        // Then
        assertThat(members).hasSize(3);
        assertThat(members.get(0).getName()).isEqualTo("Bob Johnson");
        assertThat(members.get(1).getName()).isEqualTo("Jane Doe");
        assertThat(members.get(2).getName()).isEqualTo("John Smith");
    }

    @Test
    public void testSaveMember() {
        // Given
        Member newMember = Member.builder()
                .name("Alice Williams")
                .email("alice.williams@mailinator.com")
                .phoneNumber("2125554545")
                .build();

        // When
        Member savedMember = memberRepository.save(newMember);
        entityManager.flush();

        // Then
        assertThat(savedMember.getId()).isNotNull();
        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Alice Williams");
        assertThat(foundMember.get().getEmail()).isEqualTo("alice.williams@mailinator.com");
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("2125554545");
    }

    @Test
    public void testDeleteMember() {
        // Given
        Long id = john.getId();

        // When
        memberRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Member> foundMember = memberRepository.findById(id);
        assertThat(foundMember).isEmpty();
    }
}
