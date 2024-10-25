package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class MemberRepositoryTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Member.class, MemberRepository.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    public void setUp() {
        testMember = new Member();
        testMember.setName("John Doe");
        testMember.setEmail("john.doe@example.com");
        testMember.setPhoneNumber("1234567890");
    }

    @Test
    public void testCreate() {
        memberRepository.create(testMember);
        assertNotNull(testMember.getId(), "Member ID should not be null after creation");
    }

    @Test
    public void testFindById() {
        memberRepository.create(testMember);
        Member foundMember = memberRepository.findById(testMember.getId());
        assertNotNull(foundMember, "Member should be found by ID");
        assertEquals(testMember.getName(), foundMember.getName(), "Member names should match");
    }

    @Test
    public void testFindAllOrderedByName() {
        Member member1 = new Member();
        member1.setName("Alice");
        member1.setEmail("alice@example.com");
        member1.setPhoneNumber("1111111111");

        Member member2 = new Member();
        member2.setName("Bob");
        member2.setEmail("bob@example.com");
        member2.setPhoneNumber("2222222222");

        memberRepository.create(member2);
        memberRepository.create(member1);
        memberRepository.create(testMember);

        List<Member> members = memberRepository.findAllOrderedByName();
        assertEquals(3, members.size(), "Should have 3 members");
        assertEquals("Alice", members.get(0).getName(), "First member should be Alice");
        assertEquals("Bob", members.get(1).getName(), "Second member should be Bob");
        assertEquals("John Doe", members.get(2).getName(), "Third member should be John Doe");
    }

    @Test
    public void testUpdate() {
        memberRepository.create(testMember);
        testMember.setName("Jane Doe");
        memberRepository.update(testMember);
        Member updatedMember = memberRepository.findById(testMember.getId());
        assertEquals("Jane Doe", updatedMember.getName(), "Member name should be updated");
    }

    @Test
    public void testDelete() {
        memberRepository.create(testMember);
        Long memberId = testMember.getId();
        memberRepository.delete(testMember);
        Member deletedMember = memberRepository.findById(memberId);
        assertNull(deletedMember, "Member should be deleted");
    }
}