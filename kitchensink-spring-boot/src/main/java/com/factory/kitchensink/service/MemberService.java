package com.factory.kitchensink.service;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing Member entities.
 * 
 * This is a migration of the original JBoss EAP kitchensink MemberRegistration EJB.
 * 
 * Migration mapping:
 * - Original: @Stateless EJB with implicit transaction management
 * - Spring Boot: @Service with explicit @Transactional annotation
 * 
 * - Original: Injected EntityManager for persistence operations
 * - Spring Boot: Injected MemberRepository (Spring Data JPA)
 * 
 * - Original: java.util.logging.Logger
 * - Spring Boot: SLF4J Logger
 * 
 * - Original: CDI events for notifying about member registration
 * - Spring Boot: No direct equivalent used; Spring application events could be used if needed
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;
    
    /**
     * Register a new member.
     * 
     * This method replaces the original register method in MemberRegistration EJB.
     * It persists the member entity and logs the registration.
     * 
     * @param member the member to register
     * @return the registered member with generated ID
     * @throws ValidationException if a member with the same email already exists
     */
    @Transactional
    public Member registerMember(Member member) {
        logger.info("Registering {}", member.getName());
        
        // Check if email already exists (replacing the validation in MemberResourceRESTService)
        if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Email address already exists");
        }
        
        return memberRepository.save(member);
    }
    
    /**
     * Find all members ordered by name.
     * 
     * @return list of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Find a member by ID.
     * 
     * @param id the member ID
     * @return the member with the specified ID or null if not found
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }
    
    /**
     * Find a member by email address.
     * 
     * @param email the email address to search for
     * @return the member with the specified email or null if not found
     */
    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Check if a member with the given email already exists.
     * 
     * This replaces the emailAlreadyExists method from MemberResourceRESTService.
     * 
     * @param email the email to check
     * @return true if the email already exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailAlreadyExists(String email) {
        return memberRepository.findByEmail(email) != null;
    }
}
