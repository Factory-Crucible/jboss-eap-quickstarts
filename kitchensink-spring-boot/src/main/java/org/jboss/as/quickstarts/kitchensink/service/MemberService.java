package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        if (emailAlreadyExists(member.getEmail())) {
            throw new Exception("Email " + member.getEmail() + " already exists");
        }
        memberRepository.save(member);
    }

    public List<Member> findAllOrderedByName() {
        log.info("Finding all members ordered by name");
        return memberRepository.findAll();
    }

    private boolean emailAlreadyExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}