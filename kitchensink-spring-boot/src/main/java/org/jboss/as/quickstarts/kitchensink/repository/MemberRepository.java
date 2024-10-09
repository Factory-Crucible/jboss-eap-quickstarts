package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Find members by last name.
     *
     * @param lastName the last name to search for
     * @return a list of members with the given last name
     */
    List<Member> findByLastName(String lastName);

    /**
     * Find members by first name.
     *
     * @param firstName the first name to search for
     * @return a list of members with the given first name
     */
    List<Member> findByFirstName(String firstName);

    /**
     * Check if a member with the given email exists.
     *
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}