package org.jboss.as.quickstarts.springboot.service;

import org.jboss.as.quickstarts.springboot.model.Member;
import org.jboss.as.quickstarts.springboot.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class MemberRegistrationService {

    private final MemberRepository memberRepository;
    private final Logger log;

    public MemberRegistrationService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.log = Logger.getLogger(MemberRegistrationService.class.getName());
    }

    @Transactional
    public void register(Member member) {
        log.info("Registering " + member.getName());
        memberRepository.save(member);
        log.info("Successfully registered " + member.getName());
    }
}