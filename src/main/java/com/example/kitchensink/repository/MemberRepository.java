package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find a member by email
    Optional<Member> findByEmail(String email);

    // Find all members ordered by name
    List<Member> findAllByOrderByNameAsc();

    // The findById method is already provided by JpaRepository
    // Optional<Member> findById(Long id);
}