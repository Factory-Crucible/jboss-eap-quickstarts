package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JPA repository tests for the {@link MemberRepository}.
 * Tests all custom query methods and basic CRUD operations.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setUp() {
        // Create and persist test members
        member1 = new Member();
        member1.setName("Charlie Brown");
        member1.setEmail("charlie.brown@example.com");
        member1.setPhoneNumber("1234567890");
        entityManager.persist(member1);

        member2 = new Member();
        member2.setName("Alice Smith");
        member2.setEmail("alice.smith@example.com");
        member2.setPhoneNumber("2345678901");
        entityManager.persist(member2);

        member3 = new Member();
        member3.setName("Bob Johnson");
        member3.setEmail("bob.johnson@example.com");
        member3.setPhoneNumber("3456789012");
        entityManager.persist(member3);

        // Flush to ensure data is in the database
        entityManager.flush();
    }

    @Test
    @DisplayName("findByEmail should return member when email exists")
    void findByEmailShouldReturnMemberWhenEmailExists() {
        // When
        Member foundMember = memberRepository.findByEmail(member1.getEmail());

        // Then
        assertNotNull(foundMember);
        assertEquals(member1.getName(), foundMember.getName());
        assertEquals(member1.getEmail(), foundMember.getEmail());
        assertEquals(member1.getPhoneNumber(), foundMember.getPhoneNumber());
    }

    @Test
    @DisplayName("findByEmail should return null when email does not exist")
    void findByEmailShouldReturnNullWhenEmailDoesNotExist() {
        // When
        Member foundMember = memberRepository.findByEmail("nonexistent@example.com");

        // Then
        assertNull(foundMember);
    }

    @Test
    @DisplayName("findAllByOrderByNameAsc should return all members ordered by name")
    void findAllByOrderByNameAscShouldReturnAllMembersOrderedByName() {
        // When
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Then
        assertNotNull(members);
        assertEquals(3, members.size());
        
        // Verify order: Alice, Bob, Charlie (alphabetical by name)
        assertEquals("Alice Smith", members.get(0).getName());
        assertEquals("Bob Johnson", members.get(1).getName());
        assertEquals("Charlie Brown", members.get(2).getName());
    }

    @Test
    @DisplayName("save should persist a new member")
    void saveShouldPersistNewMember() {
        // Given
        Member newMember = new Member();
        newMember.setName("David Wilson");
        newMember.setEmail("david.wilson@example.com");
        newMember.setPhoneNumber("4567890123");

        // When
        Member savedMember = memberRepository.save(newMember);

        // Then
        assertNotNull(savedMember.getId());
        assertEquals(newMember.getName(), savedMember.getName());
        assertEquals(newMember.getEmail(), savedMember.getEmail());
        assertEquals(newMember.getPhoneNumber(), savedMember.getPhoneNumber());

        // Verify it's in the database
        Member foundMember = entityManager.find(Member.class, savedMember.getId());
        assertNotNull(foundMember);
        assertEquals(newMember.getName(), foundMember.getName());
    }

    @Test
    @DisplayName("findById should return member when ID exists")
    void findByIdShouldReturnMemberWhenIdExists() {
        // When
        Optional<Member> foundMember = memberRepository.findById(member1.getId());

        // Then
        assertTrue(foundMember.isPresent());
        assertEquals(member1.getName(), foundMember.get().getName());
        assertEquals(member1.getEmail(), foundMember.get().getEmail());
    }

    @Test
    @DisplayName("findById should return empty optional when ID does not exist")
    void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        // When
        Optional<Member> foundMember = memberRepository.findById(999L);

        // Then
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("delete should remove member from database")
    void deleteShouldRemoveMemberFromDatabase() {
        // Given
        Long memberId = member2.getId();

        // When
        memberRepository.delete(member2);
        entityManager.flush();
        entityManager.clear();

        // Then
        Member foundMember = entityManager.find(Member.class, memberId);
        assertNull(foundMember);
    }

    @Test
    @DisplayName("update should modify existing member")
    void updateShouldModifyExistingMember() {
        // Given
        String updatedName = "Charlie Brown Updated";
        String updatedPhone = "9876543210";
        
        member1.setName(updatedName);
        member1.setPhoneNumber(updatedPhone);

        // When
        Member updatedMember = memberRepository.save(member1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Member foundMember = entityManager.find(Member.class, member1.getId());
        assertNotNull(foundMember);
        assertEquals(updatedName, foundMember.getName());
        assertEquals(updatedPhone, foundMember.getPhoneNumber());
        assertEquals(member1.getEmail(), foundMember.getEmail()); // Email should remain unchanged
    }

    @Test
    @DisplayName("count should return correct number of members")
    void countShouldReturnCorrectNumberOfMembers() {
        // When
        long count = memberRepository.count();

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("findAll should return all members")
    void findAllShouldReturnAllMembers() {
        // When
        List<Member> members = memberRepository.findAll();

        // Then
        assertNotNull(members);
        assertEquals(3, members.size());
        assertThat(members).extracting(Member::getEmail)
            .containsExactlyInAnyOrder(
                member1.getEmail(),
                member2.getEmail(),
                member3.getEmail()
            );
    }
}
