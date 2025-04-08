package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Member} entities.
 * This interface provides CRUD operations and custom finder methods for Member entities.
 * It replaces the original JBoss EAP kitchensink MemberRepository class.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email the email address to search for
     * @return the member with the specified email address, or null if none found
     */
    Member findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email the email address to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
