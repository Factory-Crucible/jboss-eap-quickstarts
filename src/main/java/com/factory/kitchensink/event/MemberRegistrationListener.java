package com.factory.kitchensink.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.factory.kitchensink.model.Member;

/**
 * Listener for member registration events.
 * This component listens for MemberRegisteredEvent and logs when a member is registered.
 */
@Component
public class MemberRegistrationListener {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberRegistrationListener.class);
    
    /**
     * Handles the MemberRegisteredEvent by logging information about the registered member.
     * 
     * @param event the event containing the registered member
     */
    @EventListener
    public void onMemberRegistered(MemberRegisteredEvent event) {
        Member member = event.getMember();
        logger.info("New member registered: {} ({})", member.getName(), member.getEmail());
        logger.debug("Member details: ID={}, Name={}, Email={}, Phone={}",
                member.getId(), member.getName(), member.getEmail(), member.getPhoneNumber());
    }
}
