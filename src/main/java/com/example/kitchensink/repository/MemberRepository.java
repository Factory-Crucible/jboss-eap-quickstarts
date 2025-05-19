package com.example.kitchensink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kitchensink.model.Member;

import java.util.List;

/**
 * Repository for Member entities.
 * This interface provides CRUD operations and custom query methods for the Member entity.
 * Spring Data JPA automatically implements this interface at runtime.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email The email to search for
     * @return The member with the given email, or null if none exists
     */
    Member findByEmail(String email);
    
    /**
     * Find all members ordered by name.
     * 
     * @return List of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
