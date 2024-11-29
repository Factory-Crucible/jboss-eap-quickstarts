package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MemberService {

    private Logger log = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        // Check if the email already exists
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new Exception("Email already exists");
        }

        memberRepository.save(member);

        eventPublisher.publishEvent(member);

        log.info(member.getName() + " was registered with email: " + member.getEmail());
    }

    // Additional business logic methods can be added here as needed
}
