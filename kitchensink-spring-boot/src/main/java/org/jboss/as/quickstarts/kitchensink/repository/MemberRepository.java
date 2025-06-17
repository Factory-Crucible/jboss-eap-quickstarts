package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Member entities.
 * This interface provides CRUD operations and custom finder methods for Member entities.
 * It replaces the original JBoss EAP MemberRepository class with Spring Data JPA functionality.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address.
     * 
     * @param email the email address to search for
     * @return the member with the specified email address
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
