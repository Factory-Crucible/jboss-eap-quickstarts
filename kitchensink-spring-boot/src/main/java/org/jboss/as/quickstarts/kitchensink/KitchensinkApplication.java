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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Kitchensink application.
 * 
 * This is a Spring Boot migration of the original JBoss EAP Kitchensink quickstart.
 * It demonstrates the migration from Jakarta EE (Java EE) to Spring Boot, replacing:
 * - CDI with Spring's dependency injection
 * - EJB with Spring @Service and @Transactional
 * - JAX-RS with Spring MVC @RestController
 * - JPA EntityManager with Spring Data JPA repositories
 * 
 * The application provides a simple member registration system with REST API endpoints.
 */
@SpringBootApplication
public class KitchensinkApplication {

    /**
     * Main method that starts the Spring Boot application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
