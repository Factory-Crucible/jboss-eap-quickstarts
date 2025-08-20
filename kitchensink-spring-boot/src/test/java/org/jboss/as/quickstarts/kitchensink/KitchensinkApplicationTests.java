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
package org.jboss.as.quickstarts.kitchensink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Main Spring Boot test class for the Kitchensink application.
 * 
 * This test verifies that the Spring application context loads correctly,
 * which is a basic sanity check that all beans are properly configured.
 */
@SpringBootTest
class KitchensinkApplicationTests {

    /**
     * Test that the application context loads successfully.
     * 
     * This test will fail if there are any issues with bean configuration,
     * component scanning, or application properties.
     */
    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads without errors
        // No assertions needed as Spring Boot will fail the test if context loading fails
    }
}
