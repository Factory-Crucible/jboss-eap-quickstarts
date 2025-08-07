package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Member entities.
 * Replaces the original JBoss kitchensink MemberRepository that used manual JPA Criteria API.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address
     * 
     * @param email The email address to search for
     * @return An Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order
     * 
     * @return List of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
