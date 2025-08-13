package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Member entity.
 * This interface provides CRUD operations and custom query methods.
 * It replaces the custom JPA implementation from the original JBoss application.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     * 
     * @param email the email to search for
     * @return the member with the specified email, or null if none found
     */
    Member findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
