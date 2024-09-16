package org.springframework.quickstarts.kitchensink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.quickstarts.kitchensink.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Custom query method to find a member by email
    Member findByEmail(String email);

    // Custom query method to find members by name (case-insensitive)
    List<Member> findByNameIgnoreCase(String name);

    // Add more custom query methods as needed based on your application requirements
}