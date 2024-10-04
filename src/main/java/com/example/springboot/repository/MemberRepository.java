package com.example.springboot.repository;

import com.example.springboot.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find member by ID (provided by JpaRepository)
    @Override
    Optional<Member> findById(Long id);

    // Find member by email
    Optional<Member> findByEmail(String email);

    // Find all members ordered by name
    @Query("SELECT m FROM Member m ORDER BY m.name ASC")
    List<Member> findAllOrderedByName();
}