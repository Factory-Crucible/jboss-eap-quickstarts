package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new Exception("Email " + member.getEmail() + " already exists");
        }

        try {
            return memberRepository.save(member);
        } catch (Exception e) {
            log.error("Error registering member", e);
            throw new Exception("Error registering member", e);
        }
    }

    public List<Member> getAllMembers() {
        log.info("Retrieving all members");
        return memberRepository.findAll();
    }

    public Member findById(Long id) {
        log.info("Finding member with id: " + id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    @Transactional
    public Member update(Member member) {
        log.info("Updating member: " + member.getName());
        if (!memberRepository.existsById(member.getId())) {
            throw new RuntimeException("Member not found with id: " + member.getId());
        }
        return memberRepository.save(member);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting member with id: " + id);
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}