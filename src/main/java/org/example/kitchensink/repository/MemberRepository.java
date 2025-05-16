package org.example.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by their email address
     * 
     * @param email the email to search for
     * @return an Optional containing the member if found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
