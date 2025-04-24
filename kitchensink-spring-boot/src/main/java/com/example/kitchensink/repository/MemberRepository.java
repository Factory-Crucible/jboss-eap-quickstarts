package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * 
 * This interface provides methods to perform CRUD operations on Member entities
 * and defines custom query methods for specific data access needs.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Finds a member by their email address.
     * 
     * @param email The email address to search for
     * @return An Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Retrieves all members ordered by name in ascending order.
     * 
     * @return A list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
