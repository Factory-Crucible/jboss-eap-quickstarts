package com.factory.kitchensink.service;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import com.factory.kitchensink.exception.MemberEmailExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member register(Member member) {
        log.info("Registering {}", member.getName());
        
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new MemberEmailExistsException("Email already exists: " + member.getEmail());
        }
        
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }
}
