package com.factory.kitchensink.service.event;

import org.springframework.context.ApplicationEvent;

import com.factory.kitchensink.model.Member;

import lombok.Getter;

/**
 * Event that is fired when a new member is registered.
 * This replaces the CDI event from the original JBoss implementation.
 */
@Getter
public class MemberRegisteredEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final Member member;

    /**
     * Creates a new MemberRegisteredEvent.
     * 
     * @param member the member that was registered
     */
    public MemberRegisteredEvent(Member member) {
        super(member);
        this.member = member;
    }
}
