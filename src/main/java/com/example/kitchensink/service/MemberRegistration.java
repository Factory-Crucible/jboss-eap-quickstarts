package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberRegistration {

    private static final Logger log = LoggerFactory.getLogger(MemberRegistration.class);

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        // Check if a member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new Exception("Email already exists: " + member.getEmail());
        }

        try {
            memberRepository.save(member);
            log.info("Successfully registered " + member.getName());
        } catch (Exception e) {
            log.error("Error registering " + member.getName(), e);
            throw new Exception("Error registering " + member.getName(), e);
        }
    }
}