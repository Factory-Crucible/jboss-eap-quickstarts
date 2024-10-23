package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class MemberRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testFindById() {
        // Given
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");
        entityManager.persist(member);
        entityManager.flush();

        // When
        Optional<Member> found = memberRepository.findById(member.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(member.getName(), found.get().getName());
        assertEquals(member.getEmail(), found.get().getEmail());
    }

    @Test
    public void testFindByEmail() {
        // Given
        Member member = new Member();
        member.setName("Jane Doe");
        member.setEmail("jane@example.com");
        member.setPhoneNumber("0987654321");
        entityManager.persist(member);
        entityManager.flush();

        // When
        Member found = memberRepository.findByEmail(member.getEmail());

        // Then
        assertNotNull(found);
        assertEquals(member.getName(), found.getName());
        assertEquals(member.getEmail(), found.getEmail());
    }

    @Test
    public void testSaveMember() {
        // Given
        Member member = new Member();
        member.setName("Bob Smith");
        member.setEmail("bob@example.com");
        member.setPhoneNumber("5555555555");

        // When
        Member saved = memberRepository.save(member);

        // Then
        assertNotNull(saved.getId());
        assertEquals(member.getName(), saved.getName());
        assertEquals(member.getEmail(), saved.getEmail());
    }

    @Test
    public void testUpdateMember() {
        // Given
        Member member = new Member();
        member.setName("Alice Johnson");
        member.setEmail("alice@example.com");
        member.setPhoneNumber("1112223333");
        entityManager.persist(member);
        entityManager.flush();

        // When
        member.setName("Alice Smith");
        Member updated = memberRepository.save(member);

        // Then
        assertEquals("Alice Smith", updated.getName());
        assertEquals(member.getId(), updated.getId());
    }

    @Test
    public void testDeleteMember() {
        // Given
        Member member = new Member();
        member.setName("Charlie Brown");
        member.setEmail("charlie@example.com");
        member.setPhoneNumber("9998887777");
        entityManager.persist(member);
        entityManager.flush();

        // When
        memberRepository.delete(member);

        // Then
        Optional<Member> deleted = memberRepository.findById(member.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testFindAllMembers() {
        // Given
        Member member1 = new Member();
        member1.setName("David Lee");
        member1.setEmail("david@example.com");
        member1.setPhoneNumber("4443332222");
        entityManager.persist(member1);

        Member member2 = new Member();
        member2.setName("Eva Green");
        member2.setEmail("eva@example.com");
        member2.setPhoneNumber("6667778888");
        entityManager.persist(member2);

        entityManager.flush();

        // When
        List<Member> members = memberRepository.findAll();

        // Then
        assertFalse(members.isEmpty());
        assertTrue(members.size() >= 2);
        assertTrue(members.stream().anyMatch(m -> m.getEmail().equals("david@example.com")));
        assertTrue(members.stream().anyMatch(m -> m.getEmail().equals("eva@example.com")));
    }
}