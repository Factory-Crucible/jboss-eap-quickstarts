package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link MemberRepository}.
 * These tests verify the repository methods using an in-memory database
 * and Spring's TestEntityManager to set up test data.
 */
@DataJpaTest
class MemberRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private MemberRepository memberRepository;
    
    private Member member1;
    private Member member2;
    private Member member3;
    
    @BeforeEach
    void setUp() {
        // Clear any existing data
        memberRepository.deleteAll();
        
        // Create and persist test members
        member1 = Member.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();
        
        member2 = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phoneNumber("0987654321")
                .build();
        
        member3 = Member.builder()
                .name("Alice Smith")
                .email("alice.smith@example.com")
                .phoneNumber("5556667777")
                .build();
        
        // Persist test data
        member1 = entityManager.persist(member1);
        member2 = entityManager.persist(member2);
        member3 = entityManager.persist(member3);
        
        // Flush to ensure data is in the database
        entityManager.flush();
    }
    
    @Test
    void findByEmail_ShouldReturnMember_WhenEmailExists() {
        // Act
        Optional<Member> found = memberRepository.findByEmail("john.doe@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals(member1.getId(), found.get().getId());
        assertEquals("John Doe", found.get().getName());
        assertEquals("john.doe@example.com", found.get().getEmail());
        assertEquals("1234567890", found.get().getPhoneNumber());
    }
    
    @Test
    void findByEmail_ShouldReturnEmptyOptional_WhenEmailDoesNotExist() {
        // Act
        Optional<Member> found = memberRepository.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(found.isPresent());
    }
    
    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMembers_WhenNameMatches() {
        // Act
        List<Member> foundMembers = memberRepository.findByNameContainingIgnoreCase("doe");
        
        // Assert
        assertEquals(2, foundMembers.size());
        assertTrue(foundMembers.stream().anyMatch(m -> m.getId().equals(member1.getId())));
        assertTrue(foundMembers.stream().anyMatch(m -> m.getId().equals(member2.getId())));
    }
    
    @Test
    void findByNameContainingIgnoreCase_ShouldReturnEmptyList_WhenNoNameMatches() {
        // Act
        List<Member> foundMembers = memberRepository.findByNameContainingIgnoreCase("nonexistent");
        
        // Assert
        assertTrue(foundMembers.isEmpty());
    }
    
    @Test
    void findByNameContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // Act - search with different case
        List<Member> foundMembersLower = memberRepository.findByNameContainingIgnoreCase("john");
        List<Member> foundMembersUpper = memberRepository.findByNameContainingIgnoreCase("JOHN");
        List<Member> foundMembersMixed = memberRepository.findByNameContainingIgnoreCase("JoHn");
        
        // Assert - all searches should return the same result
        assertEquals(1, foundMembersLower.size());
        assertEquals(1, foundMembersUpper.size());
        assertEquals(1, foundMembersMixed.size());
        
        assertEquals(member1.getId(), foundMembersLower.get(0).getId());
        assertEquals(member1.getId(), foundMembersUpper.get(0).getId());
        assertEquals(member1.getId(), foundMembersMixed.get(0).getId());
    }
    
    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Act
        boolean exists = memberRepository.existsByEmail("john.doe@example.com");
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Act
        boolean exists = memberRepository.existsByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(exists);
    }
    
    @Test
    void searchMembers_ShouldReturnMembers_WhenTermMatchesNameOrEmail() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - search by name
        Page<Member> resultsByName = memberRepository.searchMembers("doe", pageable);
        
        // Assert
        assertEquals(2, resultsByName.getTotalElements());
        assertTrue(resultsByName.getContent().stream().anyMatch(m -> m.getId().equals(member1.getId())));
        assertTrue(resultsByName.getContent().stream().anyMatch(m -> m.getId().equals(member2.getId())));
        
        // Act - search by email
        Page<Member> resultsByEmail = memberRepository.searchMembers("jane.doe", pageable);
        
        // Assert
        assertEquals(1, resultsByEmail.getTotalElements());
        assertEquals(member2.getId(), resultsByEmail.getContent().get(0).getId());
    }
    
    @Test
    void searchMembers_ShouldReturnEmptyPage_WhenNoMatches() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act
        Page<Member> results = memberRepository.searchMembers("nonexistent", pageable);
        
        // Assert
        assertEquals(0, results.getTotalElements());
        assertTrue(results.getContent().isEmpty());
    }
    
    @Test
    void searchMembers_ShouldBeCaseInsensitive() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Act - search with different case
        Page<Member> resultsLower = memberRepository.searchMembers("john", pageable);
        Page<Member> resultsUpper = memberRepository.searchMembers("JOHN", pageable);
        Page<Member> resultsMixed = memberRepository.searchMembers("JoHn", pageable);
        
        // Assert - all searches should return the same result
        assertEquals(1, resultsLower.getTotalElements());
        assertEquals(1, resultsUpper.getTotalElements());
        assertEquals(1, resultsMixed.getTotalElements());
        
        assertEquals(member1.getId(), resultsLower.getContent().get(0).getId());
        assertEquals(member1.getId(), resultsUpper.getContent().get(0).getId());
        assertEquals(member1.getId(), resultsMixed.getContent().get(0).getId());
    }
    
    @Test
    void findByPhoneNumber_ShouldReturnMembers_WhenPhoneNumberMatches() {
        // Act
        List<Member> foundMembers = memberRepository.findByPhoneNumber("1234567890");
        
        // Assert
        assertEquals(1, foundMembers.size());
        assertEquals(member1.getId(), foundMembers.get(0).getId());
    }
    
    @Test
    void findByPhoneNumber_ShouldReturnEmptyList_WhenNoPhoneNumberMatches() {
        // Act
        List<Member> foundMembers = memberRepository.findByPhoneNumber("9999999999");
        
        // Assert
        assertTrue(foundMembers.isEmpty());
    }
    
    @Test
    void countByNameContainingIgnoreCase_ShouldReturnCorrectCount() {
        // Act
        long doeCount = memberRepository.countByNameContainingIgnoreCase("doe");
        long smithCount = memberRepository.countByNameContainingIgnoreCase("smith");
        long nonexistentCount = memberRepository.countByNameContainingIgnoreCase("nonexistent");
        
        // Assert
        assertEquals(2, doeCount);
        assertEquals(1, smithCount);
        assertEquals(0, nonexistentCount);
    }
}
