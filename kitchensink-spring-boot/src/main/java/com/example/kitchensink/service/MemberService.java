package com.example.kitchensink.service;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Member register(Member member) {
        log.info("Registering new member: {}", member.getName());
        
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        Member savedMember = memberRepository.save(member);
        eventPublisher.publishEvent(savedMember);
        return savedMember;
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    public List<Member> findAllOrderedByName() {
        return memberRepository.findAllByOrderByNameAsc();
    }
}
