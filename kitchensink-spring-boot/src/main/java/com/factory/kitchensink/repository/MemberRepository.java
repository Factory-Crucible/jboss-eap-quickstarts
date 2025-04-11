package com.factory.kitchensink.repository;

import com.factory.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * This interface provides methods to interact with the database for Member entities.
 * It extends JpaRepository to inherit basic CRUD operations and pagination support.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Finds a member by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Retrieves all members ordered by name in ascending order.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
    
    /**
     * Checks if a member with the given email exists.
     * 
     * @param email the email address to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
