package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     *
     * @param email the email address to search for
     * @return the member with the given email address, or null if not found
     */
    Member findByEmail(String email);

    /**
     * Find members by last name.
     *
     * @param lastName the last name to search for
     * @return a list of members with the given last name
     */
    List<Member> findByLastName(String lastName);
}