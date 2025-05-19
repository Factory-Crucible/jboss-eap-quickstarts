package com.example.kitchensink.event;

import org.springframework.context.ApplicationEvent;

import com.example.kitchensink.model.Member;

/**
 * Event that is fired when a new member is registered.
 * This class replaces the CDI event mechanism used in the original JBoss application
 * with Spring's event publishing system.
 */
public class MemberRegisteredEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final Member member;
    
    /**
     * Create a new MemberRegisteredEvent.
     *
     * @param member The member that was registered
     */
    public MemberRegisteredEvent(Member member) {
        super(member);
        this.member = member;
    }
    
    /**
     * Get the registered member.
     *
     * @return The member that was registered
     */
    public Member getMember() {
        return member;
    }
}
