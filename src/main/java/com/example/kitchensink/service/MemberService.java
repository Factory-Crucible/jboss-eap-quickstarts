package com.example.kitchensink.service;

import com.example.kitchensink.exception.MemberRegistrationException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member register(Member member) {
        log.info("Registering {}", member.getName());
        
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new MemberRegistrationException("Email already exists: " + member.getEmail());
        }
        
        try {
            return memberRepository.save(member);
        } catch (Exception e) {
            throw new MemberRegistrationException("Error registering member", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new MemberRegistrationException("Member not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberRegistrationException("Member not found with email: " + email));
    }
}
