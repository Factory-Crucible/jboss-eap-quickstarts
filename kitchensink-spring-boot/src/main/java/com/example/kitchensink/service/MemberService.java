package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member registerMember(Member member) {
        // Check if a member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}