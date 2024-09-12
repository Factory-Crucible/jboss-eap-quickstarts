package org.springframework.boot.kitchensink.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testMemberCreation() {
        Member member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");

        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getName()).isEqualTo("John Doe");
        assertThat(member.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(member.getPhoneNumber()).isEqualTo("1234567890");
    }

    @Test
    void testNameValidation() {
        Member member = new Member();
        member.setName("John123");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Must not contain numbers");
    }

    @Test
    void testEmailValidation() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("invalid-email");
        member.setPhoneNumber("1234567890");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must be a well-formed email address");
    }

    @Test
    void testPhoneNumberValidation() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("123");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("size must be between 10 and 12");
    }

    @Test
    void testValidMember() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }
}