package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member register(Member member) {
        log.info("Registering " + member.getName());
        // Check if member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new RuntimeException("Email already exists: " + member.getEmail());
        }
        member.setRegistrationDate();
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Member updateMember(Member member) {
        if (!memberRepository.existsById(member.getId())) {
            throw new RuntimeException("Member not found with id: " + member.getId());
        }
        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    public List<Member> findMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
}