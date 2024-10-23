package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testMemberCreation() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");

        assertNotNull(member);
        assertEquals("John Doe", member.getName());
        assertEquals("john.doe@example.com", member.getEmail());
        assertEquals("1234567890", member.getPhoneNumber());
    }

    @Test
    public void testNameValidation() {
        Member member = new Member();
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");

        // Test null name
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("Name is required", violations.iterator().next().getMessage());

        // Test empty name
        member.setName("");
        violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("Name is required", violations.iterator().next().getMessage());

        // Test valid name
        member.setName("John Doe");
        violations = validator.validate(member);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmailValidation() {
        Member member = new Member();
        member.setName("John Doe");
        member.setPhoneNumber("1234567890");

        // Test null email
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());

        // Test invalid email format
        member.setEmail("invalid-email");
        violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());

        // Test valid email
        member.setEmail("john.doe@example.com");
        violations = validator.validate(member);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testPhoneNumberValidation() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");

        // Test null phone number
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertTrue(violations.isEmpty()); // Assuming phone number is optional

        // Test invalid phone number format
        member.setPhoneNumber("123");
        violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("Invalid phone number format", violations.iterator().next().getMessage());

        // Test valid phone number
        member.setPhoneNumber("1234567890");
        violations = validator.validate(member);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testGettersAndSetters() {
        Member member = new Member();

        member.setId(1L);
        assertEquals(1L, member.getId());

        member.setName("Jane Doe");
        assertEquals("Jane Doe", member.getName());

        member.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", member.getEmail());

        member.setPhoneNumber("9876543210");
        assertEquals("9876543210", member.getPhoneNumber());
    }
}