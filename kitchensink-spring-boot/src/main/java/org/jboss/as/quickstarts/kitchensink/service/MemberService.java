package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final Logger log = Logger.getLogger(MemberService.class.getName());

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering " + member.getName());

        // Check if a member with the same email already exists
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new Exception("Email already exists");
        }

        try {
            memberRepository.save(member);
            return member;
        } catch (ConstraintViolationException e) {
            throw new Exception("Invalid data: " + e.getMessage());
        }
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Page<Member> findAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Member updateMember(Member member) throws Exception {
        if (member.getId() == null) {
            throw new Exception("Member ID cannot be null for update operation");
        }

        if (!memberRepository.existsById(member.getId())) {
            throw new Exception("Member not found with id: " + member.getId());
        }

        try {
            return memberRepository.save(member);
        } catch (ConstraintViolationException e) {
            throw new Exception("Invalid data: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteMember(Long id) throws Exception {
        if (!memberRepository.existsById(id)) {
            throw new Exception("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    public long getTotalMemberCount() {
        return memberRepository.count();
    }
}