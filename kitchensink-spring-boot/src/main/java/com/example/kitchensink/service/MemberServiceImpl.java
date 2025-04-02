package com.example.kitchensink.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kitchensink.exception.EmailAlreadyExistsException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;

/**
 * Implementation of {@link MemberService} that uses JPA for database operations.
 * This class handles the business logic for member management.
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    private Logger log = Logger.getLogger(MemberServiceImpl.class.getName());
    
    private final MemberRepository memberRepository;
    
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    /**
     * {@inheritDoc}
     * 
     * This implementation validates that the email is unique before persisting the member.
     * If the email is already in use, an EmailAlreadyExistsException is thrown.
     */
    @Override
    @Transactional
    public Member register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        
        // Check if the email already exists
        if (emailExists(member.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + member.getEmail() + " already exists");
        }
        
        // Save the member
        return memberRepository.save(member);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembersOrderedByName() {
        return memberRepository.findAllOrderedByName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
