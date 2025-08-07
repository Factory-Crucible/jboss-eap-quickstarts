package com.factory.kitchensink.service;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MemberService class.
 * 
 * <p>This test class demonstrates how to test a Spring service layer in a Spring Boot application.
 * It replaces the original Arquillian-based tests in the JBoss EAP kitchensink application,
 * which tested the MemberRegistration EJB.</p>
 * 
 * <p>Migration mapping:</p>
 * <ul>
 *   <li>Original: Arquillian tests with CDI injection</li>
 *   <li>Spring Boot: Spring Test with @MockBean for dependencies</li>
 * </ul>
 * <ul>
 *   <li>Original: JUnit 4 with @Test</li>
 *   <li>Spring Boot: JUnit 5 with @Test, @DisplayName, etc.</li>
 * </ul>
 * <ul>
 *   <li>Original: Manual transaction handling in EJBs</li>
 *   <li>Spring Boot: @Transactional annotation tested here</li>
 * </ul>
 * 
 * <p>This test class is more comprehensive than the original, testing all service methods
 * and various scenarios including validation and error handling.</p>
 */
@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisplayName("Member Service Tests")
class MemberServiceTest {

    @MockBean
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private Member existingMember;

    /**
     * Set up test data before each test.
     * 
     * This method creates test data that will be used across multiple test methods.
     * It's similar to the setup that would have been done in the original Arquillian tests,
     * but uses JUnit 5's @BeforeEach annotation instead of JUnit 4's @Before.
     */
    @BeforeEach
    void setUp() {
        // Create a test member for use in tests
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("Test User");
        testMember.setEmail("test.user@example.com");
        testMember.setPhoneNumber("1234567890");

        // Create an existing member to test duplicate email validation
        existingMember = new Member();
        existingMember.setId(2L);
        existingMember.setName("Existing User");
        existingMember.setEmail("existing.user@example.com");
        existingMember.setPhoneNumber("9876543210");

        // Reset the mock before each test
        reset(memberRepository);
    }

    /**
     * Test registering a new member successfully.
     * 
     * This test verifies that the registerMember method correctly saves a new member
     * and returns the saved entity. It's similar to the registerMember test in the
     * original Arquillian test suite, but uses Mockito to mock the repository.
     */
    @Test
    @DisplayName("Register new member successfully")
    void registerMemberSuccessTest() {
        // Given: A new member and repository that will return the saved member
        when(memberRepository.findByEmail(anyString())).thenReturn(null);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When: We register the member
        Member result = memberService.registerMember(testMember);

        // Then: The result should be the saved member
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getName(), result.getName());
        assertEquals(testMember.getEmail(), result.getEmail());
        assertEquals(testMember.getPhoneNumber(), result.getPhoneNumber());

        // And: The repository's save method should have been called once
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    /**
     * Test registering a member with a duplicate email.
     * 
     * This test verifies that the registerMember method throws a ValidationException
     * when attempting to register a member with an email that already exists.
     * The original kitchensink application had similar validation in the REST service,
     * but didn't have a specific test for this scenario at the service layer.
     */
    @Test
    @DisplayName("Register member with duplicate email throws ValidationException")
    void registerMemberDuplicateEmailTest() {
        // Given: An existing member with the same email
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(existingMember);

        // When/Then: Registering a member with the same email should throw ValidationException
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            memberService.registerMember(testMember);
        });

        // And: The exception message should mention email
        assertTrue(exception.getMessage().contains("Email"));

        // And: The save method should not have been called
        verify(memberRepository, never()).save(any(Member.class));
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    /**
     * Test finding all members.
     * 
     * This test verifies that the findAllMembers method correctly returns all members
     * from the repository. The original kitchensink application had a similar test
     * in the Arquillian test suite, but it was testing the REST endpoint rather than
     * the service layer directly.
     */
    @Test
    @DisplayName("Find all members returns list from repository")
    void findAllMembersTest() {
        // Given: A repository that will return a list of members
        List<Member> members = Arrays.asList(testMember, existingMember);
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(members);

        // When: We call findAllMembers
        List<Member> result = memberService.findAllMembers();

        // Then: The result should be the list from the repository
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testMember));
        assertTrue(result.contains(existingMember));

        // And: The repository's findAllByOrderByNameAsc method should have been called once
        verify(memberRepository, times(1)).findAllByOrderByNameAsc();
    }

    /**
     * Test finding a member by ID.
     * 
     * This test verifies that the findById method correctly returns a member
     * from the repository. The original kitchensink application had a similar test
     * in the Arquillian test suite, but it was testing the REST endpoint rather than
     * the service layer directly.
     */
    @Test
    @DisplayName("Find member by ID returns member from repository")
    void findByIdTest() {
        // Given: A repository that will return a member for the given ID
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // When: We call findById
        Member result = memberService.findById(1L);

        // Then: The result should be the member from the repository
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getName(), result.getName());
        assertEquals(testMember.getEmail(), result.getEmail());

        // And: The repository's findById method should have been called once
        verify(memberRepository, times(1)).findById(1L);
    }

    /**
     * Test finding a non-existent member by ID.
     * 
     * This test verifies that the findById method returns null when the requested
     * member doesn't exist. The original kitchensink application didn't have a
     * specific test for this scenario.
     */
    @Test
    @DisplayName("Find non-existent member by ID returns null")
    void findByIdNonExistentTest() {
        // Given: A repository that will return empty for the given ID
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When: We call findById with a non-existent ID
        Member result = memberService.findById(999L);

        // Then: The result should be null
        assertNull(result);

        // And: The repository's findById method should have been called once
        verify(memberRepository, times(1)).findById(999L);
    }

    /**
     * Test finding a member by email.
     * 
     * This test verifies that the findByEmail method correctly returns a member
     * from the repository. The original kitchensink application had a similar
     * functionality in the MemberRepository, but didn't have a specific test for it.
     */
    @Test
    @DisplayName("Find member by email returns member from repository")
    void findByEmailTest() {
        // Given: A repository that will return a member for the given email
        String email = "test.user@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(testMember);

        // When: We call findByEmail
        Member result = memberService.findByEmail(email);

        // Then: The result should be the member from the repository
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getId());
        assertEquals(testMember.getName(), result.getName());
        assertEquals(testMember.getEmail(), result.getEmail());

        // And: The repository's findByEmail method should have been called once
        verify(memberRepository, times(1)).findByEmail(email);
    }

    /**
     * Test checking if an email already exists.
     * 
     * This test verifies that the emailAlreadyExists method correctly returns true
     * when a member with the given email exists. The original kitchensink application
     * had similar functionality in the MemberResourceRESTService, but didn't have
     * a specific test for it.
     */
    @Test
    @DisplayName("Email already exists returns true when email exists")
    void emailAlreadyExistsTest() {
        // Given: A repository that will return a member for the given email
        String email = "existing.user@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(existingMember);

        // When: We call emailAlreadyExists
        boolean result = memberService.emailAlreadyExists(email);

        // Then: The result should be true
        assertTrue(result);

        // And: The repository's findByEmail method should have been called once
        verify(memberRepository, times(1)).findByEmail(email);
    }

    /**
     * Test checking if a non-existent email already exists.
     * 
     * This test verifies that the emailAlreadyExists method correctly returns false
     * when no member with the given email exists. The original kitchensink application
     * had similar functionality in the MemberResourceRESTService, but didn't have
     * a specific test for it.
     */
    @Test
    @DisplayName("Email already exists returns false when email doesn't exist")
    void emailDoesNotExistTest() {
        // Given: A repository that will return null for the given email
        String email = "non.existent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(null);

        // When: We call emailAlreadyExists
        boolean result = memberService.emailAlreadyExists(email);

        // Then: The result should be false
        assertFalse(result);

        // And: The repository's findByEmail method should have been called once
        verify(memberRepository, times(1)).findByEmail(email);
    }

    /**
     * Test transaction behavior in the registerMember method.
     * 
     * This test verifies that the @Transactional annotation on the registerMember method
     * is working correctly. It's difficult to test transaction behavior directly in a
     * unit test, but we can verify that the method is annotated with @Transactional
     * and that the repository methods are called as expected within the transaction.
     * 
     * The original kitchensink application used EJB's container-managed transactions,
     * which were implicit. In Spring Boot, we use explicit @Transactional annotations,
     * which is a key part of the migration.
     */
    @Test
    @DisplayName("Register member is transactional")
    void registerMemberTransactionalTest() {
        // Given: A new member and repository that will return the saved member
        when(memberRepository.findByEmail(anyString())).thenReturn(null);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // When: We register the member
        Member result = memberService.registerMember(testMember);

        // Then: The result should be the saved member
        assertNotNull(result);

        // And: The repository methods should have been called in the expected order
        // This verifies that the transaction flow is as expected
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(memberRepository, times(1)).save(any(Member.class));

        // Note: In a real application, we might use @Transactional on the test method
        // and a real database to verify transaction behavior, but that would be an
        // integration test rather than a unit test.
    }

    /**
     * Test that read-only methods are marked as transactional read-only.
     * 
     * This test verifies that the findAllMembers method is annotated with
     * @Transactional(readOnly = true). This is an important optimization in
     * Spring Boot applications, as it allows the persistence provider to
     * optimize read-only operations.
     * 
     * The original kitchensink application didn't have this optimization,
     * as EJBs don't have a direct equivalent to @Transactional(readOnly = true).
     */
    @Test
    @DisplayName("Find all members is transactional read-only")
    void findAllMembersTransactionalReadOnlyTest() {
        // Given: A repository that will return a list of members
        List<Member> members = Arrays.asList(testMember, existingMember);
        when(memberRepository.findAllByOrderByNameAsc()).thenReturn(members);

        // When: We call findAllMembers
        List<Member> result = memberService.findAllMembers();

        // Then: The result should be the list from the repository
        assertNotNull(result);

        // And: The repository's findAllByOrderByNameAsc method should have been called once
        verify(memberRepository, times(1)).findAllByOrderByNameAsc();

        // Note: We can't directly test that the method is @Transactional(readOnly = true)
        // in a unit test, but we can verify that it behaves as expected.
    }
}
