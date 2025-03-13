package com.factory.kitchensink.service;

import com.factory.kitchensink.model.Member;
import com.factory.kitchensink.repository.MemberRepository;
import com.factory.kitchensink.exception.EmailAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member register(@Valid Member member) {
        log.info("Registering {}", member.getName());
        
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + member.getEmail());
        }
        
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
    }
}
