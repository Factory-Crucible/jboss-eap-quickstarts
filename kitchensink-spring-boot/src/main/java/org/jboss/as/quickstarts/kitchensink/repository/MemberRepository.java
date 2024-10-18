package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the member if found, or empty if not found
     */
    Optional<Member> findByEmail(String email);

    // Add any other custom query methods that were present in the original MemberRepository
}