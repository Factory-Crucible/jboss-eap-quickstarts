package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * This interface provides CRUD operations and custom finder methods.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by their email address.
     * 
     * @param email the email to search for
     * @return the member with the given email, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
