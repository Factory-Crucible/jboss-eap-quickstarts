package com.example.kitchensink.event;

import org.springframework.context.ApplicationEvent;

import com.example.kitchensink.model.Member;

/**
 * Event that is published when a new member is registered.
 * This event is the Spring equivalent of the CDI event used in the JBoss application.
 * Components can listen for this event using @EventListener annotation.
 */
public class MemberRegisteredEvent extends ApplicationEvent {
    
    /**
     * Creates a new MemberRegisteredEvent.
     *
     * @param member the newly registered member
     */
    public MemberRegisteredEvent(Member member) {
        super(member);
    }
    
    /**
     * Gets the member that was registered.
     *
     * @return the registered member
     */
    public Member getMember() {
        return (Member) getSource();
    }
}
