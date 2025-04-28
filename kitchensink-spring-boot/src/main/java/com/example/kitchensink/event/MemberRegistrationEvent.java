package com.example.kitchensink.event;

import org.springframework.context.ApplicationEvent;

import com.example.kitchensink.model.Member;

/**
 * Event that is fired when a member is registered or updated.
 * This event can be listened to by components that need to react to member changes.
 */
public class MemberRegistrationEvent extends ApplicationEvent {
    
    private final Member member;
    
    /**
     * Create a new MemberRegistrationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param member the member that was registered or updated
     */
    public MemberRegistrationEvent(Object source, Member member) {
        super(source);
        this.member = member;
    }
    
    /**
     * Get the member that was registered or updated.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }
}
