package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Member entities.
 * Replaces the original CDI-based MemberRepository.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address
     * 
     * @param email the email to search for
     * @return the member with the specified email
     */
    Member findByEmail(String email);
    
    /**
     * Find all members ordered by name
     * 
     * @return list of all members ordered by name ascending
     */
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}
