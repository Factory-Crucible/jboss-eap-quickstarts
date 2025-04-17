package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * Spring Data JPA automatically implements this interface with standard CRUD operations.
 * Custom query methods are defined for specific business requirements.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     * 
     * @param email The email address to search for
     * @return An Optional containing the member if found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name.
     * 
     * @return List of all members sorted by name
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
    
    /**
     * Check if a member exists with the given email.
     * 
     * @param email The email to check
     * @return true if a member exists with the email, false otherwise
     */
    boolean existsByEmail(String email);
}
