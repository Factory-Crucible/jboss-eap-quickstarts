package org.jboss.as.quickstarts.kitchensink.integration;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test for the MemberRepository.
 * This test uses an actual H2 database to verify repository functionality.
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MemberRepositoryIntegrationTest {

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

        // Save the test data
        john = memberRepository.save(john);
        alice = memberRepository.save(alice);
        bob = memberRepository.save(bob);
    }

    @Test
    public void testFindById_ShouldReturnCorrectMember() {
        // When
        Optional<Member> foundMember = memberRepository.findById(john.getId());

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("John Smith");
        assertThat(foundMember.get().getEmail()).isEqualTo("john@example.com");
        assertThat(foundMember.get().getPhoneNumber()).isEqualTo("1234567890");
    }

    @Test
    public void testFindByEmail_ShouldReturnCorrectMember() {
        // When
        Optional<Member> foundMember = memberRepository.findByEmail("alice@example.com");

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("Alice Johnson");
    }

    @Test
    public void testFindByEmail_WithNonexistentEmail_ShouldReturnEmpty() {
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
    public void testSaveMember_WithNewMember_ShouldPersistMember() {
        // Given
        Member emma = Member.builder()
                .name("Emma Wilson")
                .email("emma@example.com")
                .phoneNumber("4567890123")
                .build();

        // When
        Member savedMember = memberRepository.save(emma);

        // Then
        assertThat(savedMember.getId()).isNotNull();
        
        // Verify it can be retrieved
        Optional<Member> retrievedMember = memberRepository.findById(savedMember.getId());
        assertThat(retrievedMember).isPresent();
        assertThat(retrievedMember.get().getName()).isEqualTo("Emma Wilson");
        assertThat(retrievedMember.get().getEmail()).isEqualTo("emma@example.com");
    }

    @Test
    public void testSaveMember_WithExistingMember_ShouldUpdateMember() {
        // Given
        john.setName("John Smith Updated");
        john.setPhoneNumber("9876543210");

        // When
        Member updatedMember = memberRepository.save(john);

        // Then
        assertThat(updatedMember.getId()).isEqualTo(john.getId());
        assertThat(updatedMember.getName()).isEqualTo("John Smith Updated");
        assertThat(updatedMember.getPhoneNumber()).isEqualTo("9876543210");
        
        // Verify it was updated in the database
        Optional<Member> retrievedMember = memberRepository.findById(john.getId());
        assertThat(retrievedMember).isPresent();
        assertThat(retrievedMember.get().getName()).isEqualTo("John Smith Updated");
        assertThat(retrievedMember.get().getPhoneNumber()).isEqualTo("9876543210");
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
        Long bobId = bob.getId();
        assertThat(memberRepository.findById(bobId)).isPresent();

        // When
        memberRepository.delete(bob);
        memberRepository.flush();

        // Then
        assertThat(memberRepository.findById(bobId)).isEmpty();
        assertThat(memberRepository.count()).isEqualTo(2);
    }

    @Test
    public void testCount_ShouldReturnCorrectNumberOfMembers() {
        // When
        long count = memberRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    public void testExistsById_WithExistingId_ShouldReturnTrue() {
        // When
        boolean exists = memberRepository.existsById(alice.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsById_WithNonexistentId_ShouldReturnFalse() {
        // When
        boolean exists = memberRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    public void testDeleteById_ShouldRemoveMemberFromDatabase() {
        // Given
        Long aliceId = alice.getId();
        assertThat(memberRepository.existsById(aliceId)).isTrue();

        // When
        memberRepository.deleteById(aliceId);
        memberRepository.flush();

        // Then
        assertThat(memberRepository.existsById(aliceId)).isFalse();
        assertThat(memberRepository.count()).isEqualTo(2);
    }
}
