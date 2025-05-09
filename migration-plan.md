# Migration Plan: JBoss Kitchensink to Spring Boot

## Overview

This document outlines the comprehensive plan for migrating the JBoss "kitchensink" application from Jakarta EE to Spring Boot 3.x. The migration will preserve all existing functionality while leveraging Spring Boot's modern architecture and development approach.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Setup](#project-setup)
3. [Domain Model Migration](#domain-model-migration)
4. [Data Access Layer Migration](#data-access-layer-migration)
5. [Business Logic Migration](#business-logic-migration)
6. [REST API Migration](#rest-api-migration)
7. [Application Properties Migration](#application-properties-migration)
8. [Exception Handling Migration](#exception-handling-migration)
9. [Bean Validation Migration](#bean-validation-migration)
10. [Testing Strategy](#testing-strategy)
11. [Deployment Considerations](#deployment-considerations)

## Prerequisites

Before beginning the migration, ensure the following prerequisites are in place:

- Java 17 or higher installed
- Maven 3.8+ or Gradle 7.5+ for build management
- IDE with Spring Boot support (IntelliJ IDEA, Eclipse, VS Code with Spring extensions)
- Git for version control
- Basic understanding of Spring Boot concepts

## Project Setup

1. Create a new Spring Boot project using [Spring Initializr](https://start.spring.io/) with the following dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database (for development)
   - Validation
   - Spring Boot DevTools
   - Lombok (optional, for reducing boilerplate code)

2. Project metadata:
   - Group: org.jboss.as.quickstarts.kitchensink
   - Artifact: kitchensink-spring
   - Name: kitchensink-spring
   - Description: Spring Boot version of the JBoss kitchensink application
   - Package name: org.jboss.as.quickstarts.kitchensink
   - Packaging: Jar
   - Java version: 17

3. Download the generated project and import it into your IDE.

4. Create the following package structure:
   ```
   src/main/java/org/jboss/as/quickstarts/kitchensink/
   ├── config
   ├── controller
   ├── exception
   ├── model
   ├── repository
   ├── service
   ├── util
   └── KitchensinkApplication.java
   ```

## Domain Model Migration

### Migrating the Member Entity

**Current JBoss Implementation:**

```java
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Member implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    @Column(name = "phone_number")
    private String phoneNumber;

    // Getters and setters
}
```

**Spring Boot Implementation:**

```java
package org.jboss.as.quickstarts.kitchensink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString
public class Member implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    @Column(name = "phone_number")
    private String phoneNumber;
}
```

**Key Changes:**
- Removed `@XmlRootElement` as we'll use Jackson for JSON serialization
- Added `strategy = GenerationType.IDENTITY` to `@GeneratedValue`
- Added Lombok `@Data` annotation (optional) to reduce boilerplate code
- Retained all validation annotations which are compatible with Spring Boot

## Data Access Layer Migration

### Migrating the Repository

**Current JBoss Implementation:**

```java
@ApplicationScoped
public class MemberRepository {

    @Inject
    private EntityManager em;

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
        Root<Member> member = criteria.from(Member.class);
        criteria.select(member).where(cb.equal(member.get("email"), email));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Member> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
        Root<Member> member = criteria.from(Member.class);
        criteria.select(member).orderBy(cb.asc(member.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
```

**Spring Boot Implementation:**

```java
package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    List<Member> findAllByOrderByNameAsc();
}
```

**Key Changes:**
- Replaced custom repository implementation with Spring Data JPA interface
- Replaced `@ApplicationScoped` with Spring's `@Repository`
- Replaced manual JPA queries with Spring Data's method naming conventions
- Changed return type of `findByEmail` to `Optional<Member>` for better null handling
- No need to manually inject `EntityManager` as Spring Data JPA handles this

## Business Logic Migration

### Migrating the Service Layer

**Current JBoss Implementation:**

```java
@Stateless
public class MemberRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Member> memberEventSrc;

    public void register(Member member) throws Exception {
        log.info("Registering " + member.getName());
        em.persist(member);
        memberEventSrc.fire(member);
    }
}
```

**Spring Boot Implementation:**

```java
package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class MemberService {

    private final Logger log;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MemberService(Logger log, MemberRepository memberRepository, 
                        ApplicationEventPublisher eventPublisher) {
        this.log = log;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Member register(Member member) {
        log.info("Registering " + member.getName());
        Member savedMember = memberRepository.save(member);
        eventPublisher.publishEvent(savedMember); // Spring event
        return savedMember;
    }
}
```

**Key Changes:**
- Replaced `@Stateless` EJB annotation with Spring's `@Service`
- Replaced CDI `@Inject` with constructor-based dependency injection
- Replaced direct `EntityManager` usage with `MemberRepository`
- Added `@Transactional` for transaction management
- Replaced CDI events with Spring's `ApplicationEventPublisher`
- Return the saved entity from the method for better usability

### Creating a Logger Configuration

```java
package org.jboss.as.quickstarts.kitchensink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class LoggerConfig {

    @Bean
    public Logger logger() {
        return Logger.getLogger("org.jboss.as.quickstarts.kitchensink");
    }
}
```

## REST API Migration

### Migrating the REST Service

**Current JBoss Implementation:**

```java
@Path("/members")
@RequestScoped
public class MemberResourceRESTService {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private MemberRepository repository;

    @Inject
    MemberRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Member> listAllMembers() {
        return repository.findAllOrderedByName();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Member lookupMemberById(@PathParam("id") long id) {
        Member member = repository.findById(id);
        if (member == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return member;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMember(Member member) {
        // Validation and registration logic
        // ...
    }
    
    // Other methods
}
```

**Spring Boot Implementation:**

```java
package org.jboss.as.quickstarts.kitchensink.controller;

import jakarta.validation.Valid;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final Logger log;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public MemberController(Logger log, MemberRepository memberRepository, 
                           MemberService memberService) {
        this.log = log;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Member not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        try {
            Member savedMember = memberService.register(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
        } catch (Exception e) {
            log.warning("Error creating member: " + e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
```

**Key Changes:**
- Replaced JAX-RS annotations with Spring MVC annotations
- Changed `@Path` to `@RequestMapping`
- Changed `@GET`, `@POST` to `@GetMapping`, `@PostMapping`
- Changed `@PathParam` to `@PathVariable`
- Changed `@Produces`, `@Consumes` to Spring's content negotiation
- Replaced JAX-RS `Response` with Spring's `ResponseEntity`
- Replaced `WebApplicationException` with Spring's `ResponseStatusException`
- Used constructor injection instead of field injection
- Added `@Valid` for automatic validation of request bodies

## Application Properties Migration

### Migrating Configuration Properties

**Current JBoss Implementation (persistence.xml):**

```xml
<persistence-unit name="primary">
   <jta-data-source>java:jboss/datasources/KitchensinkQuickstartDS</jta-data-source>
   <properties>
      <property name="hibernate.hbm2ddl.auto" value="create-drop" />
      <property name="hibernate.show_sql" value="false" />
   </properties>
</persistence-unit>
```

**Spring Boot Implementation (application.properties):**

```properties
# DataSource Configuration
spring.datasource.url=jdbc:h2:mem:kitchensink;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Server Configuration
server.port=8080
server.servlet.context-path=/kitchensink

# Logging Configuration
logging.level.org.jboss.as.quickstarts.kitchensink=INFO
logging.level.org.springframework=INFO
logging.level.org.hibernate=WARN

# H2 Console (for development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Key Changes:**
- Replaced `persistence.xml` with Spring Boot's `application.properties`
- Configured datasource properties directly in the properties file
- Added server configuration
- Added logging configuration
- Added H2 console configuration for development

## Exception Handling Migration

### Creating a Global Exception Handler

```java
package org.jboss.as.quickstarts.kitchensink.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

**Key Changes:**
- Created a centralized exception handler with `@RestControllerAdvice`
- Added handlers for validation exceptions
- Added a generic exception handler
- Structured error responses consistently

## Bean Validation Migration

Spring Boot uses the same Jakarta Bean Validation API, so most validation annotations can be kept as-is. The main difference is in how validation is triggered and how validation errors are handled.

### Creating a Custom Validator for Email Uniqueness

```java
package org.jboss.as.quickstarts.kitchensink.service;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberValidationService {

    private final MemberRepository memberRepository;

    public MemberValidationService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean isEmailUnique(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }
    
    public void validateMember(Member member) {
        if (!isEmailUnique(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
    }
}
```

Then update the `MemberService` to use this validation:

```java
@Service
public class MemberService {
    // Other fields...
    private final MemberValidationService validationService;

    public MemberService(Logger log, MemberRepository memberRepository, 
                        ApplicationEventPublisher eventPublisher,
                        MemberValidationService validationService) {
        // Initialize other fields...
        this.validationService = validationService;
    }

    @Transactional
    public Member register(Member member) {
        log.info("Registering " + member.getName());
        validationService.validateMember(member);
        Member savedMember = memberRepository.save(member);
        eventPublisher.publishEvent(savedMember);
        return savedMember;
    }
}
```

## Testing Strategy

1. **Unit Tests**: Create unit tests for each service and controller using JUnit 5 and Mockito.

2. **Integration Tests**: Use Spring Boot Test to create integration tests that test the entire application flow.

Example integration test:

```java
package org.jboss.as.quickstarts.kitchensink;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testCreateMember() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        ResponseEntity<Member> response = restTemplate.postForEntity(
            "/api/members", member, Member.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
    }
}
```

## Deployment Considerations

1. **Application Packaging**:
   - Spring Boot applications are typically packaged as executable JARs
   - Use `./mvnw package` or `./gradlew build` to create the JAR

2. **Deployment Options**:
   - Standalone: `java -jar kitchensink-spring.jar`
   - Docker: Create a Dockerfile to containerize the application
   - Kubernetes: Create deployment YAML files for Kubernetes

3. **Sample Dockerfile**:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

4. **Environment-Specific Configuration**:
   - Create `application-dev.properties`, `application-prod.properties` for different environments
   - Use Spring profiles to activate the appropriate configuration: `--spring.profiles.active=prod`

5. **Database Migration**:
   - Consider using Flyway or Liquibase for database schema migration
   - This allows for versioned database schema changes

## Conclusion

This migration plan provides a comprehensive approach to transitioning the JBoss kitchensink application to Spring Boot 3.x. By following these steps, you'll create a modern, maintainable application that preserves all the functionality of the original while leveraging Spring Boot's powerful features and conventions.

Remember to test thoroughly at each step of the migration to ensure that functionality is preserved and that the application behaves as expected in the new Spring Boot environment.
