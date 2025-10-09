package com.factory.kitchensink.service;

import com.factory.kitchensink.data.MemberRepository;
import com.factory.kitchensink.model.Member;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Member register(Member member) {
        try {
            return repository.save(member);
        } catch (DataIntegrityViolationException e) {
            // rethrow to be mapped by exception handler as 409
            throw e;
        }
    }
}
