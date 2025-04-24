package com.example.kitchensink.event;

import com.example.kitchensink.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener for member registration events.
 * 
 * This component listens for MemberRegisteredEvent and performs
 * actions when a new member is registered, such as logging the event
 * or triggering additional business processes.
 */
@Slf4j
@Component
public class MemberRegistrationListener implements ApplicationListener<MemberRegisteredEvent> {

    /**
     * Handles the member registration event.
     * 
     * @param event The MemberRegisteredEvent containing the registered member
     */
    @Override
    public void onApplicationEvent(MemberRegisteredEvent event) {
        Member member = event.getMember();
        log.info("New member registered: {} ({})", member.getName(), member.getEmail());
        
        // Additional actions could be performed here, such as:
        // - Sending a welcome email
        // - Updating statistics
        // - Notifying other systems
    }
}
