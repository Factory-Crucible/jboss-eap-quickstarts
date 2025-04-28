package com.example.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kitchensink.model.Member;

/**
 * Repository for Member entity operations.
 * Provides methods to find members by ID, email, and to retrieve all members ordered by name.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by their email address
     * 
     * @param email the email to search for
     * @return the member with the given email, or empty if none found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name
     * 
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
    
    /**
     * Check if a member with the given email exists
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
