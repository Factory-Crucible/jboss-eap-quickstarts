/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensink.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MemberRegistrationIT {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addClasses(Member.class, MemberRegistration.class, Resources.class)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(new StringAsset("<beans xmlns=\"https://jakarta.ee/xml/ns/jakartaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd\"\n"
                        + "bean-discovery-mode=\"all\">\n"
                        + "</beans>"), "beans.xml")
            // Deploy our test datasource
            .addAsWebInfResource("test-ds.xml");
    }

    @Inject
    MemberRegistration memberRegistration;

    @Inject
    Logger log;

    @Test
    public void testRegister() throws Exception {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane@mailinator.com");
        newMember.setPhoneNumber("2125551234");
        memberRegistration.register(newMember);
        assertNotNull(newMember.getId());
        log.info(newMember.getName() + " was persisted with id " + newMember.getId());
    }

}

    @Test
    @RunAsClient
    public void testRegisterWithInvalidEmail() throws Exception {
        Member newMember = new Member();
        newMember.setName("John Doe");
        newMember.setEmail("invalid-email");
        newMember.setPhoneNumber("1234567890");
        
        try {
            memberRegistration.register(newMember);
            fail("Expected an exception for invalid email");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Invalid email format"));
        }
    }

    @Test
    @RunAsClient
    public void testRegisterWithEmptyName() throws Exception {
        Member newMember = new Member();
        newMember.setName("");
        newMember.setEmail("john@example.com");
        newMember.setPhoneNumber("1234567890");
        
        try {
            memberRegistration.register(newMember);
            fail("Expected an exception for empty name");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }
    }

    @Test
    @RunAsClient
    public void testRegisterDuplicateEmail() throws Exception {
        Member member1 = new Member();
        member1.setName("John Doe");
        member1.setEmail("john@example.com");
        member1.setPhoneNumber("1234567890");
        memberRegistration.register(member1);

        Member member2 = new Member();
        member2.setName("Jane Doe");
        member2.setEmail("john@example.com");
        member2.setPhoneNumber("9876543210");

        try {
            memberRegistration.register(member2);
            fail("Expected an exception for duplicate email");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Email already exists"));
        }
    }

    @Test
    @RunAsClient
    public void testConcurrentRegistrations() throws Exception {
        int numThreads = 10;
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    Member member = new Member();
                    member.setName("Concurrent User " + index);
                    member.setEmail("user" + index + "@example.com");
                    member.setPhoneNumber("123456789" + index);
                    memberRegistration.register(member);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.warning("Failed to register member: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await(30, TimeUnit.SECONDS);
        assertEquals("All concurrent registrations should succeed", numThreads, successCount.get());
    }
}
