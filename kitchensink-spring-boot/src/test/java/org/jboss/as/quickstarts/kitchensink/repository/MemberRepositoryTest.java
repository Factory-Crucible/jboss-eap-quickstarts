package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the MemberRepository.
 * Tests the custom finder methods and JPA functionality.
 */
@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member john;
    private Member alice;
    private Member bob;

    @BeforeEach
    public void setup() {
        // Clear any existing data
        memberRepository.deleteAll();

        // Create test members with names in non-alphabetical order
        john = Member.builder()
                .name("John Smith")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .build();

        alice = Member.builder()
                .name("Alice Johnson")
                .email("alice@example.com")
                .phoneNumber("2345678901")
                .build();

        bob = Member.builder()
                .name("Bob Williams")
                .email("bob@example.com")
                .phoneNumber("3456789012")
                .build();

        // Persist the test data
        entityManager.persist(john);
        entityManager.persist(alice);
        entityManager.persist(bob);
        entityManager.flush();
    }

    @Test
    public void testFindByEmail_WhenEmailExists_ShouldReturnMember() {
        // When
        Optional<Member> foundMember = memberRepository.findByEmail("john@example.com");

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("John Smith");
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("1234567890");
    }

    @Test
    public void testFindByEmail_WhenEmailDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Member> foundMember = memberRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundMember).isEmpty();
    }

    @Test
    public void testFindAllByOrderByNameAsc_ShouldReturnMembersInAlphabeticalOrder() {
        // When
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Then
        assertThat(members).hasSize(3);
        assertThat(members.get(0).getName()).isEqualTo("Alice Johnson");
        assertThat(members.get(1).getName()).isEqualTo("Bob Williams");
        assertThat(members.get(2).getName()).isEqualTo("John Smith");
    }

    @Test
    public void testSaveMember_WithUniqueEmail_ShouldPersistMember() {
        // Given
        Member newMember = Member.builder()
                .name("Emma Wilson")
                .email("emma@example.com")
                .phoneNumber("4567890123")
                .build();

        // When
        Member savedMember = memberRepository.save(newMember);

        // Then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(memberRepository.findById(savedMember.getId())).isPresent();
    }

    @Test
    public void testSaveMember_WithDuplicateEmail_ShouldThrowException() {
        // Given
        Member duplicateEmailMember = Member.builder()
                .name("Another John")
                .email("john@example.com") // Same email as john
                .phoneNumber("5678901234")
                .build();

        // When/Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberRepository.saveAndFlush(duplicateEmailMember);
        });
    }

    @Test
    public void testDeleteMember_ShouldRemoveMemberFromDatabase() {
        // Given
        Long johnId = john.getId();
        assertThat(memberRepository.findById(johnId)).isPresent();

        // When
        memberRepository.delete(john);
        entityManager.flush();

        // Then
        assertThat(memberRepository.findById(johnId)).isEmpty();
        assertThat(memberRepository.findAll()).hasSize(2);
    }

    @Test
    public void testFindById_WhenIdExists_ShouldReturnMember() {
        // When
        Optional<Member> foundMember = memberRepository.findById(alice.getId());

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Alice Johnson");
    }

    @Test
    public void testFindById_WhenIdDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Member> foundMember = memberRepository.findById(999L);

        // Then
        assertThat(foundMember).isEmpty();
    }
}
