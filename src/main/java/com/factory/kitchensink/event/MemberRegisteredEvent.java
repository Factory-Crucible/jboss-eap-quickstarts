package com.factory.kitchensink.event;

import org.springframework.context.ApplicationEvent;

import com.factory.kitchensink.model.Member;

/**
 * Event that is fired when a new member is registered.
 * This event can be listened to by components that need to perform
 * actions after member registration, such as sending welcome emails.
 */
public class MemberRegisteredEvent extends ApplicationEvent {
    
    private final Member member;
    
    /**
     * Constructs a new MemberRegisteredEvent.
     * 
     * @param member the member that was registered
     */
    public MemberRegisteredEvent(Member member) {
        super(member);
        this.member = member;
    }
    
    /**
     * Gets the member that was registered.
     * 
     * @return the registered member
     */
    public Member getMember() {
        return member;
    }
}
