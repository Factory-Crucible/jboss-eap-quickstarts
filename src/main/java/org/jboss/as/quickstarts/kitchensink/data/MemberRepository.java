package org.jboss.as.quickstarts.kitchensink.data;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Member entities
 * Uses Spring Data JPA for automatic implementation of basic CRUD operations
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
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
