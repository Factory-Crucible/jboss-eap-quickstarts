package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Service for Member registration and management.
 * Replaces the original JBoss kitchensink MemberRegistration EJB.
 */
@Service
@Validated
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    
    private final MemberRepository memberRepository;

    /**
     * Constructor injection of dependencies
     * 
     * @param memberRepository The repository for Member entities
     */
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Register a new member
     * 
     * @param member The member to register
     * @return The registered member with generated ID
     * @throws EmailAlreadyExistsException if a member with the same email already exists
     */
    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering {}", member.getName());
        
        // Check if email already exists
        if (emailExists(member.getEmail())) {
            log.error("Email {} already exists", member.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        return memberRepository.save(member);
    }

    /**
     * Find all members ordered by name
     * 
     * @return List of all members ordered by name
     */
    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    /**
     * Find a member by ID
     * 
     * @param id The ID to search for
     * @return An Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Find a member by email
     * 
     * @param email The email to search for
     * @return An Optional containing the member if found
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * Check if a member with the given email already exists
     * 
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    private boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Exception thrown when attempting to register a member with an email that already exists
     */
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}
