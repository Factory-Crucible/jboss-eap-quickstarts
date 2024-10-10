package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Custom query method to find members by email
    Member findByEmail(String email);

    // Custom query method to find members by name
    List<Member> findByNameLikeOrderByName(String name);
}