package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Member entities.
 * Provides methods to interact with the database.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email the email to search for
     * @return the member with the specified email, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
