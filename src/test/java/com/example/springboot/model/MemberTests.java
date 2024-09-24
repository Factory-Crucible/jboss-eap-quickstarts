package com.example.springboot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.springboot.repository.MemberRepository;

@SpringBootTest
public class MemberTests {

    private Validator validator;
    private Member member;

    @MockBean
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");
    }

    @Test
    void validateMemberFields() {
        // Test valid member
        var violations = validator.validate(member);
        assertTrue(violations.isEmpty());

        // Test invalid name (null)
        member.setName(null);
        violations = validator.validate(member);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());

        // Test invalid name (contains numbers)
        member.setName("John123");
        violations = validator.validate(member);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());

        // Reset name and test invalid email
        member.setName("John Doe");
        member.setEmail("invalid-email");
        violations = validator.validate(member);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());

        // Test invalid phone number (too short)
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("123");
        violations = validator.validate(member);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("phoneNumber", violations.iterator().next().getPropertyPath().toString());

        // Test invalid phone number (contains non-digits)
        member.setPhoneNumber("123-456-7890");
        violations = validator.validate(member);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("phoneNumber", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testMemberPersistence() {
        // Mock save operation
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Mock findById operation
        when(memberRepository.findById(1L)).thenReturn(java.util.Optional.of(member));

        // Test save
        Member savedMember = memberRepository.save(member);
        assertNotNull(savedMember);
        assertEquals(member.getId(), savedMember.getId());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getPhoneNumber(), savedMember.getPhoneNumber());

        // Test retrieve
        java.util.Optional<Member> retrievedMember = memberRepository.findById(1L);
        assertTrue(retrievedMember.isPresent());
        assertEquals(member.getId(), retrievedMember.get().getId());
        assertEquals(member.getName(), retrievedMember.get().getName());
        assertEquals(member.getEmail(), retrievedMember.get().getEmail());
        assertEquals(member.getPhoneNumber(), retrievedMember.get().getPhoneNumber());

        // Verify that save and findById were called
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberRepository, times(1)).findById(1L);
    }
}