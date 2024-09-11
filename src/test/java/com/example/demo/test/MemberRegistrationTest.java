package com.example.demo.test;

import com.example.demo.model.Member;
import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRegistrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member validMember;

    @BeforeEach
    void setUp() {
        validMember = Member.builder()
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .build();
    }

    @Test
    void testCreateValidMember() {
        Member savedMember = memberRepository.save(validMember);
        assertNotNull(savedMember.getId());
        assertEquals(validMember.getName(), savedMember.getName());
        assertEquals(validMember.getEmail(), savedMember.getEmail());
        assertEquals(validMember.getPhoneNumber(), savedMember.getPhoneNumber());
    }

    @Test
    void testCreateMemberWithInvalidName() {
        Member invalidMember = Member.builder()
                .name("John123")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(invalidMember);
        });
    }

    @Test
    void testCreateMemberWithInvalidEmail() {
        Member invalidMember = Member.builder()
                .name("John Doe")
                .email("invalid-email")
                .phoneNumber("1234567890")
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(invalidMember);
        });
    }

    @Test
    void testCreateMemberWithInvalidPhoneNumber() {
        Member invalidMember = Member.builder()
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("123")
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(invalidMember);
        });
    }

    @Test
    void testUniqueEmailConstraint() {
        memberRepository.save(validMember);

        Member duplicateEmailMember = Member.builder()
                .name("Jane Doe")
                .email("john@example.com")
                .phoneNumber("9876543210")
                .build();

        assertThrows(Exception.class, () -> {
            memberRepository.save(duplicateEmailMember);
            entityManager.flush();
        });
    }
}