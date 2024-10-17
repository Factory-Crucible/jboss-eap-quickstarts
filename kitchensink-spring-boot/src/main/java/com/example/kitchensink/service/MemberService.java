package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        // Check if member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new Exception("Email already exists: " + member.getEmail());
        }

        try {
            memberRepository.save(member);
            log.info("Registered " + member.getName());
            return member;
        } catch (Exception e) {
            log.error("Error registering " + member.getName(), e);
            throw new Exception("Error registering " + member.getName(), e);
        }
    }

    public List<Member> findAllMembers() {
        log.info("Finding all members");
        return memberRepository.findAll();
    }

    public Optional<Member> findById(Long id) {
        log.info("Finding member with id: " + id);
        return memberRepository.findById(id);
    }

    @Transactional
    public Member updateMember(Member member) throws Exception {
        log.info("Updating member: " + member.getName());
        if (!memberRepository.existsById(member.getId())) {
            throw new Exception("Member not found with id: " + member.getId());
        }
        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) throws Exception {
        log.info("Deleting member with id: " + id);
        if (!memberRepository.existsById(id)) {
            throw new Exception("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}