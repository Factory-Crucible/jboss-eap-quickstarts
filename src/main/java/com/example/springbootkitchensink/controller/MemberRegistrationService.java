package com.example.springbootkitchensink.service;

import com.example.springbootkitchensink.model.Member;
import com.example.springbootkitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class MemberRegistrationService {

    private final Logger log = Logger.getLogger(MemberRegistrationService.class.getName());
    private final MemberRepository memberRepository;

    @Autowired
    public MemberRegistrationService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        try {
            memberRepository.save(member);
            // In Spring, we don't need to fire events manually as in JBoss
            // Spring's @Transactional will handle the transaction
        } catch (Exception e) {
            log.severe("Error registering member: " + e.getMessage());
            throw new Exception("Registration failed", e);
        }
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found with email: " + email));
    }
}