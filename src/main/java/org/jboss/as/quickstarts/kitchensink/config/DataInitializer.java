package org.jboss.as.quickstarts.kitchensink.config;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;

    public DataInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is empty
        if (memberRepository.count() == 0) {
            // Create and save sample Member entities
            Member member1 = new Member();
            member1.setName("John Doe");
            member1.setEmail("john.doe@example.com");
            member1.setPhoneNumber("1234567890");
            memberRepository.save(member1);

            Member member2 = new Member();
            member2.setName("Jane Smith");
            member2.setEmail("jane.smith@example.com");
            member2.setPhoneNumber("9876543210");
            memberRepository.save(member2);

            Member member3 = new Member();
            member3.setName("Bob Johnson");
            member3.setEmail("bob.johnson@example.com");
            member3.setPhoneNumber("5555555555");
            memberRepository.save(member3);

            System.out.println("Sample data has been initialized.");
        }
    }
}