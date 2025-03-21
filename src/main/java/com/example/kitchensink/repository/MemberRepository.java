package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, String> {
    
    Optional<Member> findByEmail(String email);
    
    @Query(sort = "{ 'name' : 1 }")
    List<Member> findAllByOrderByNameAsc();
    
    boolean existsByEmail(String email);
}
