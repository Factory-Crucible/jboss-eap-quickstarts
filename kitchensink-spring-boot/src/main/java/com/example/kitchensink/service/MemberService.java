package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member register(Member member) {
        log.info("Registering new member: {}", member.getName());

        // Check if member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            log.error("Email {} already exists. Registration failed.", member.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        Member savedMember = memberRepository.save(member);
        log.info("Successfully registered member with ID: {}", savedMember.getId());
        return savedMember;
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        log.info("Retrieving all members");
        return memberRepository.findAll();
    }
}