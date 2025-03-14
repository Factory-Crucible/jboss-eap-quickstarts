package com.example.kitchensink.service;

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    @Transactional
    public Member createMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new EmailAlreadyExistsException(member.getEmail());
        }
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(Long id, Member member) {
        Member existingMember = getMemberById(id);
        if (!existingMember.getEmail().equals(member.getEmail()) && 
            memberRepository.existsByEmail(member.getEmail())) {
            throw new EmailAlreadyExistsException(member.getEmail());
        }
        member.setId(id);
        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
