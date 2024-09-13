package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Spring Data JPA will automatically implement basic CRUD operations
    // Custom query methods can be added here if needed in the future
}