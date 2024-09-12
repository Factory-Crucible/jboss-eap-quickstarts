package com.migration.service;

import com.migration.model.Member;
import com.migration.repository.MemberRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class MemberRegistrationService {

    private final MemberRepository memberRepository;
    private final Validator validator;

    @Autowired
    public MemberRegistrationService(MemberRepository memberRepository, Validator validator) {
        this.memberRepository = memberRepository;
        this.validator = validator;
    }

    @Transactional
    public Member register(Member member) throws Exception {
        // Validate the member using Bean Validation
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            throw new Exception("Invalid member data: " + violations);
        }

        // Check if a member with the given email already exists
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new Exception("Email already exists: " + member.getEmail());
        }

        try {
            // Persist the member
            return memberRepository.save(member);
        } catch (Exception e) {
            throw new Exception("Error registering member", e);
        }
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public void update(Member member) throws Exception {
        if (member.getId() == null) {
            throw new Exception("Member ID is required for update");
        }
        memberRepository.save(member);
    }

    @Transactional
    public void delete(Long id) throws Exception {
        memberRepository.deleteById(id);
    }
}