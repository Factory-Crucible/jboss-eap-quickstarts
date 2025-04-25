package org.jboss.as.quickstarts.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Member} entities.
 * Extends Spring Data JPA's JpaRepository to provide CRUD operations and custom finder methods.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by their email address.
     * 
     * @param email the email address to search for
     * @return the member with the specified email, or empty if none found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Check if a member exists with the given email address.
     * 
     * @param email the email address to check
     * @return true if a member exists with the email, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all members ordered by name.
     * 
     * @return a list of all members ordered by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}
