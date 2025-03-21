package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.exception.EmailAlreadyExistsException;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Validated
@Slf4j
public class MemberService {

    private final MemberRepository repository;

    @Autowired
    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public Member register(@Valid Member member) {
        log.info("Registering " + member.getName());
        
        if (repository.existsByEmail(member.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        return repository.save(member);
    }

    public List<Member> getAllMembers() {
        return repository.findAllByOrderByNameAsc();
    }

    public Member findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member findByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Member not found"));
    }
}
