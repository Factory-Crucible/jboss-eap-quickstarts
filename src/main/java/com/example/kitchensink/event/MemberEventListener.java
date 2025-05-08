package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for member-related events.
 * This component listens for domain events related to members and performs
 * additional actions when these events occur.
 */
@Component
public class MemberEventListener {

    private final Logger logger = LoggerFactory.getLogger(MemberEventListener.class);

    /**
     * Handles the member registered event.
     * This method is called when a new member is registered in the system.
     *
     * @param event the member registered event
     */
    @EventListener
    public void handleMemberRegisteredEvent(MemberRegisteredEvent event) {
        Member member = event.getMember();
        logger.info("Member registered event received: Member [id={}, name={}, email={}]", 
                member.getId(), member.getName(), member.getEmail());
        
        // Additional processing can be added here if needed
        // For example: sending welcome emails, notifications, etc.
    }
}
