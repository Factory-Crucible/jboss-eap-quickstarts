package com.example.kitchensink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.kitchensink.model.Member;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m ORDER BY m.name")
    List<Member> findAllOrderedByName();

    Member findByEmail(String email);
}
