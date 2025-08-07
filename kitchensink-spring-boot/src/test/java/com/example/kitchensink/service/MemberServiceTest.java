package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService.EmailAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MemberService.
 * Tests the service layer with actual repository integration.
 */
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // Create a test member for use in tests
        testMember = new Member();
        testMember.setName("Test User");
        testMember.setEmail("test.user@example.com");
        testMember.setPhoneNumber("1234567890");
        
        // Clear any existing data
        memberRepository.deleteAll();
    }

    @Test
    void register_WithValidMember_ShouldRegisterSuccessfully() {
        // Given: A valid member
        
        // When: Registering the member
        Member registeredMember = memberService.register(testMember);
        
        // Then: Member should be registered with an ID
        assertNotNull(registeredMember);
        assertNotNull(registeredMember.getId());
        assertEquals(testMember.getName(), registeredMember.getName());
        assertEquals(testMember.getEmail(), registeredMember.getEmail());
        assertEquals(testMember.getPhoneNumber(), registeredMember.getPhoneNumber());
        
        // Verify the member was actually saved in the database
        Optional<Member> foundMember = memberRepository.findById(registeredMember.getId());
        assertTrue(foundMember.isPresent());
        assertEquals(testMember.getEmail(), foundMember.get().getEmail());
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        // Given: A member already registered
        memberService.register(testMember);
        
        // When: Trying to register another member with the same email
        Member duplicateEmailMember = new Member();
        duplicateEmailMember.setName("Another User");
        duplicateEmailMember.setEmail(testMember.getEmail()); // Same email
        duplicateEmailMember.setPhoneNumber("9876543210");
        
        // Then: EmailAlreadyExistsException should be thrown
        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            memberService.register(duplicateEmailMember);
        });
        
        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void register_WithInvalidMember_ShouldThrowConstraintViolationException() {
        // Given: An invalid member (null name, which violates @NotNull)
        Member invalidMember = new Member();
        invalidMember.setName(null); // Violates @NotNull
        invalidMember.setEmail("valid.email@example.com");
        invalidMember.setPhoneNumber("1234567890");
        
        // Then: ConstraintViolationException should be thrown
        assertThrows(ConstraintViolationException.class, () -> {
            memberService.register(invalidMember);
        });
    }

    @Test
    void findAll_ShouldReturnAllMembersOrderedByName() {
        // Given: Multiple members with different names
        Member member1 = new Member();
        member1.setName("Charlie");
        member1.setEmail("charlie@example.com");
        member1.setPhoneNumber("1234567890");
        
        Member member2 = new Member();
        member2.setName("Alice");
        member2.setEmail("alice@example.com");
        member2.setPhoneNumber("1234567890");
        
        Member member3 = new Member();
        member3.setName("Bob");
        member3.setEmail("bob@example.com");
        member3.setPhoneNumber("1234567890");
        
        memberService.register(member1);
        memberService.register(member2);
        memberService.register(member3);
        
        // When: Finding all members
        List<Member> members = memberService.findAll();
        
        // Then: All members should be returned in alphabetical order by name
        assertEquals(3, members.size());
        assertEquals("Alice", members.get(0).getName());
        assertEquals("Bob", members.get(1).getName());
        assertEquals("Charlie", members.get(2).getName());
    }

    @Test
    void findById_WithExistingId_ShouldReturnMember() {
        // Given: A registered member
        Member registeredMember = memberService.register(testMember);
        Long memberId = registeredMember.getId();
        
        // When: Finding by ID
        Optional<Member> foundMember = memberService.findById(memberId);
        
        // Then: Member should be found
        assertTrue(foundMember.isPresent());
        assertEquals(memberId, foundMember.get().getId());
        assertEquals(testMember.getName(), foundMember.get().getName());
        assertEquals(testMember.getEmail(), foundMember.get().getEmail());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // Given: A non-existent member ID
        Long nonExistentId = 999L;
        
        // When: Finding by non-existent ID
        Optional<Member> foundMember = memberService.findById(nonExistentId);
        
        // Then: No member should be found
        assertTrue(foundMember.isEmpty());
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnMember() {
        // Given: A registered member
        memberService.register(testMember);
        String email = testMember.getEmail();
        
        // When: Finding by email
        Optional<Member> foundMember = memberService.findByEmail(email);
        
        // Then: Member should be found
        assertTrue(foundMember.isPresent());
        assertEquals(email, foundMember.get().getEmail());
        assertEquals(testMember.getName(), foundMember.get().getName());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmptyOptional() {
        // Given: A non-existent email
        String nonExistentEmail = "nonexistent@example.com";
        
        // When: Finding by non-existent email
        Optional<Member> foundMember = memberService.findByEmail(nonExistentEmail);
        
        // Then: No member should be found
        assertTrue(foundMember.isEmpty());
    }
}
