package com.example.kitchensink.config;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for initializing sample data in the database.
 * This class creates sample members when the application starts.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final MemberRepository memberRepository;

    /**
     * Initialize the database with sample data.
     * This method is executed after the application context is loaded.
     *
     * @return a CommandLineRunner that populates the database
     */
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Initializing database with sample data");
            
            // Check if database is empty
            if (memberRepository.count() == 0) {
                log.info("Database is empty, creating sample members");
                
                // Create sample members
                Member member1 = Member.builder()
                        .name("John Smith")
                        .email("john.smith@mailinator.com")
                        .phoneNumber("2125551212")
                        .build();
                
                Member member2 = Member.builder()
                        .name("Jane Doe")
                        .email("jane.doe@mailinator.com")
                        .phoneNumber("2125552323")
                        .build();
                
                Member member3 = Member.builder()
                        .name("Bob Johnson")
                        .email("bob.johnson@mailinator.com")
                        .phoneNumber("2125553434")
                        .build();
                
                // Save sample members
                memberRepository.save(member1);
                memberRepository.save(member2);
                memberRepository.save(member3);
                
                log.info("Sample data initialization completed");
            } else {
                log.info("Database already contains data, skipping initialization");
            }
        };
    }
}
