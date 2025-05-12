/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Member entity.
 * Tests the bean validation constraints defined on the Member entity.
 */
public class MemberTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidMember() {
        // Create a valid Member object
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there are no validation errors
        assertTrue(violations.isEmpty(), "Valid member should not have constraint violations");
    }

    @Test
    public void testNameNull() {
        // Create a Member with null name
        Member member = new Member();
        member.setName(null);
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the name field
        assertFalse(violations.isEmpty(), "Member with null name should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("name", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the name field");
    }

    @Test
    public void testNameTooLong() {
        // Create a Member with a name that exceeds the maximum length
        Member member = new Member();
        member.setName("ThisNameIsWayTooLongForTheValidationConstraintAndShouldFail");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the name field
        assertFalse(violations.isEmpty(), "Member with too long name should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("name", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the name field");
    }

    @Test
    public void testNameWithNumbers() {
        // Create a Member with a name containing numbers
        Member member = new Member();
        member.setName("John123");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the name field
        assertFalse(violations.isEmpty(), "Member with name containing numbers should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("name", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the name field");
        assertEquals("Must not contain numbers", violations.iterator().next().getMessage(), "Should have the correct error message");
    }

    @Test
    public void testEmailNull() {
        // Create a Member with null email
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail(null);
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the email field
        assertFalse(violations.isEmpty(), "Member with null email should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("email", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the email field");
    }

    @Test
    public void testEmailEmpty() {
        // Create a Member with empty email
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("");
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the email field
        assertFalse(violations.isEmpty(), "Member with empty email should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("email", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the email field");
    }

    @Test
    public void testEmailInvalid() {
        // Create a Member with invalid email format
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("not-an-email");
        member.setPhoneNumber("1234567890");

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the email field
        assertFalse(violations.isEmpty(), "Member with invalid email should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("email", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the email field");
    }

    @Test
    public void testPhoneNumberNull() {
        // Create a Member with null phone number
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber(null);

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the phoneNumber field
        assertFalse(violations.isEmpty(), "Member with null phone number should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("phoneNumber", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the phoneNumber field");
    }

    @Test
    public void testPhoneNumberTooShort() {
        // Create a Member with a phone number that is too short
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("123456789"); // 9 digits, minimum is 10

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the phoneNumber field
        assertFalse(violations.isEmpty(), "Member with too short phone number should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("phoneNumber", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the phoneNumber field");
    }

    @Test
    public void testPhoneNumberTooLong() {
        // Create a Member with a phone number that is too long
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890123"); // 13 digits, maximum is 12

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the phoneNumber field
        assertFalse(violations.isEmpty(), "Member with too long phone number should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("phoneNumber", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the phoneNumber field");
    }

    @Test
    public void testPhoneNumberNonNumeric() {
        // Create a Member with a phone number containing non-numeric characters
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("123-456-7890"); // Contains non-numeric characters

        // Validate the Member object
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        
        // Assert that there is a validation error for the phoneNumber field
        assertFalse(violations.isEmpty(), "Member with non-numeric phone number should have constraint violations");
        assertEquals(1, violations.size(), "Should have exactly one constraint violation");
        assertEquals("phoneNumber", violations.iterator().next().getPropertyPath().toString(), "Violation should be for the phoneNumber field");
    }
}
