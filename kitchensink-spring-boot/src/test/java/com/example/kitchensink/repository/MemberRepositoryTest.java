package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setName("John Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhoneNumber("1234567890");
        entityManager.persist(testMember);
        entityManager.flush();
    }

    @Test
    void testSaveMember() {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane.doe@example.com");
        newMember.setPhoneNumber("9876543210");

        Member savedMember = memberRepository.save(newMember);

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("Jane Doe");
        assertThat(savedMember.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(savedMember.getPhoneNumber()).isEqualTo("9876543210");
    }

    @Test
    void testFindByEmail() {
        Optional<Member> foundMember = memberRepository.findByEmail("john.doe@example.com");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindAll() {
        List<Member> members = memberRepository.findAll();

        assertThat(members).isNotEmpty();
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void testUpdateMember() {
        testMember.setName("John Updated");
        Member updatedMember = memberRepository.save(testMember);

        assertThat(updatedMember.getName()).isEqualTo("John Updated");
    }

    @Test
    void testDeleteMember() {
        memberRepository.delete(testMember);

        Optional<Member> deletedMember = memberRepository.findById(testMember.getId());
        assertThat(deletedMember).isEmpty();
    }

    @Test
    void testFindByNonExistentEmail() {
        Optional<Member> nonExistentMember = memberRepository.findByEmail("nonexistent@example.com");

        assertThat(nonExistentMember).isEmpty();
    }
}