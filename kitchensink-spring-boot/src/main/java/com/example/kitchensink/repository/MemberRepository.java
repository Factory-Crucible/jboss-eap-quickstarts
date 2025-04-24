package com.example.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kitchensink.model.Member;

/**
 * Repository interface for {@link Member} entities.
 * This interface extends Spring Data JPA's JpaRepository to provide
 * CRUD operations and custom query methods for Member entities.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the found Member or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
