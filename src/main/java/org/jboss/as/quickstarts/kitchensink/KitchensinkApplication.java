package org.jboss.as.quickstarts.kitchensink;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * Entry point for the application with initial data loading.
 */
@SpringBootApplication
@Slf4j
public class KitchensinkApplication {

    /**
     * Main method to launch the Spring Boot application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
    
    /**
     * CommandLineRunner to initialize sample data
     * This replaces the functionality of import.sql in the JBoss version
     * 
     * @param memberRepository repository for Member entities
     * @return CommandLineRunner that loads initial data
     */
    @Bean
    public CommandLineRunner initData(MemberRepository memberRepository) {
        return args -> {
            log.info("Initializing sample data");
            
            // Create initial member (equivalent to what was in import.sql)
            if (memberRepository.count() == 0) {
                Member member = new Member();
                member.setName("John Smith");
                member.setEmail("john.smith@mailinator.com");
                member.setPhoneNumber("2125551212");
                
                memberRepository.save(member);
                log.info("Created sample member: {}", member.getName());
            }
            
            log.info("Sample data initialization complete");
        };
    }
}
