package com.example.kitchensink.integration;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MemberRepository using Testcontainers with MySQL.
 * These tests verify that repository operations work correctly with a real MySQL database.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;

    /**
     * Configure Spring Boot to use the MySQL container for testing.
     */
    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
    }

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        memberRepository.deleteAll();

        // Create test members
        member1 = Member.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();

        member2 = Member.builder()
                .name("Alice Smith")
                .email("alice.smith@example.com")
                .phoneNumber("9876543210")
                .build();

        // Save test members
        memberRepository.saveAll(Arrays.asList(member1, member2));
    }

    @Test
    void testFindById_ExistingId_ShouldReturnMember() {
        // Act
        Optional<Member> foundMember = memberRepository.findById(member1.getId());

        // Assert
        assertTrue(foundMember.isPresent());
        assertEquals(member1.getName(), foundMember.get().getName());
        assertEquals(member1.getEmail(), foundMember.get().getEmail());
    }

    @Test
    void testFindById_NonExistingId_ShouldReturnEmptyOptional() {
        // Act
        Optional<Member> foundMember = memberRepository.findById(999L);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    void testFindByEmail_ExistingEmail_ShouldReturnMember() {
        // Act
        Optional<Member> foundMember = memberRepository.findByEmail(member1.getEmail());

        // Assert
        assertTrue(foundMember.isPresent());
        assertEquals(member1.getId(), foundMember.get().getId());
        assertEquals(member1.getName(), foundMember.get().getName());
    }

    @Test
    void testFindByEmail_NonExistingEmail_ShouldReturnEmptyOptional() {
        // Act
        Optional<Member> foundMember = memberRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    void testFindAllOrderedByName_ShouldReturnMembersOrderedByName() {
        // Act
        List<Member> members = memberRepository.findAllOrderedByName();

        // Assert
        assertThat(members).hasSize(2);
        // Alice comes before John alphabetically
        assertEquals("Alice Smith", members.get(0).getName());
        assertEquals("John Doe", members.get(1).getName());
    }

    @Test
    void testSaveMember_ValidMember_ShouldPersistMember() {
        // Arrange
        Member newMember = Member.builder()
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .phoneNumber("5551234567")
                .build();

        // Act
        Member savedMember = memberRepository.save(newMember);

        // Assert
        assertNotNull(savedMember.getId());
        assertEquals(newMember.getName(), savedMember.getName());
        assertEquals(newMember.getEmail(), savedMember.getEmail());

        // Verify it's in the database
        Optional<Member> retrievedMember = memberRepository.findById(savedMember.getId());
        assertTrue(retrievedMember.isPresent());
    }

    @Test
    void testSaveMember_DuplicateEmail_ShouldThrowException() {
        // Arrange
        Member duplicateEmailMember = Member.builder()
                .name("Different Name")
                .email(member1.getEmail()) // Using existing email
                .phoneNumber("5559876543")
                .build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberRepository.save(duplicateEmailMember);
            memberRepository.flush(); // Force the exception to be thrown
        });
    }

    @Test
    void testDeleteMember_ExistingMember_ShouldRemoveMember() {
        // Act
        memberRepository.delete(member1);

        // Assert
        assertFalse(memberRepository.findById(member1.getId()).isPresent());
        assertEquals(1, memberRepository.count()); // Only member2 should remain
    }

    @Test
    void testCount_ShouldReturnCorrectNumberOfMembers() {
        // Act
        long count = memberRepository.count();

        // Assert
        assertEquals(2, count);
    }
}
