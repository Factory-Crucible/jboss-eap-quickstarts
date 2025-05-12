
# Migration Plan: JBoss EAP 'Kitchensink' to Spring Boot

## Table of Contents
- [Introduction](#introduction)
- [Migration Overview](#migration-overview)
- [Component Mapping](#component-mapping)
- [Phase 1: Project Setup](#phase-1-project-setup)
- [Phase 2: Core Application Migration](#phase-2-core-application-migration)
- [Phase 3: Testing and Validation](#phase-3-testing-and-validation)
- [Phase 4: Deployment and Documentation](#phase-4-deployment-and-documentation)
- [Timeline Estimate](#timeline-estimate)
- [Potential Challenges and Mitigation](#potential-challenges-and-mitigation)
- [Resources and References](#resources-and-references)

## Introduction

This document outlines the plan for migrating the JBoss EAP 'kitchensink' application to a Spring Boot application. The migration will preserve all existing functionality while updating the technology stack to leverage Spring Boot's features and conventions.

### Current Application Architecture

The 'kitchensink' application is a Jakarta EE application demonstrating various Jakarta EE features:
- **Data Layer**: JPA with Hibernate as the implementation
- **Dependency Injection**: CDI
- **Validation**: Jakarta Bean Validation
- **REST API**: JAX-RS
- **Service Layer**: EJB with transaction management
- **UI**: JSF (Jakarta Server Faces)

### Target Architecture

The migrated application will use Spring Boot 3.x with:
- **Data Layer**: Spring Data JPA
- **Dependency Injection**: Spring DI
- **Validation**: Spring Boot Validation (built on Jakarta Bean Validation)
- **REST API**: Spring MVC with RestControllers
- **Service Layer**: Spring Services with @Transactional
- **UI**: To be determined in a separate phase (options include Thymeleaf, React, Angular)

## Migration Overview

The migration will be executed in four phases:

1. **Project Setup**: Establish the Spring Boot project structure and environment
2. **Core Application Migration**: Migrate domain model, data access, business logic, REST API
3. **Testing and Validation**: Ensure functionality is preserved through comprehensive testing
4. **Deployment and Documentation**: Prepare for production deployment

## Component Mapping

| Jakarta EE Component | Spring Boot Equivalent | Migration Complexity |
|----------------------|------------------------|----------------------|
| JPA Entity (`@Entity`) | Spring Data JPA Entity (`@Entity`) | Low |
| CDI (`@Inject`) | Spring DI (`@Autowired`) | Low |
| Bean Validation | Spring Boot Validation | Low |
| JAX-RS (`@Path`, `@GET`, etc.) | Spring MVC (`@RestController`, `@GetMapping`, etc.) | Medium |
| EJB (`@Stateless`) | Spring Service (`@Service`, `@Transactional`) | Medium |
| EntityManager operations | Spring Data Repository methods | Medium |
| CDI Events | Spring Events | Medium |
| Exception Handling | Spring `@ExceptionHandler` and `@ControllerAdvice` | Medium |
| JSF Views | (To be addressed in UI migration phase) | High |

## Phase 1: Project Setup

### 1.1 Create Spring Boot Project Structure

1. Create a new Spring Boot project using Spring Initializr with the following dependencies:
   - Spring Web
   - Spring Data JPA
   - Validation
   - H2 Database (for development/testing)
   - Spring Boot DevTools
   - Lombok (optional, for reducing boilerplate)

2. Configure project metadata:
   ```xml
   <groupId>org.jboss.as.quickstarts</groupId>
   <artifactId>kitchensink-spring</artifactId>
   <version>1.0.0</version>
   ```

3. Set up the package structure:
   ```
   src/main/java/org/jboss/as/quickstarts/kitchensink/
   ├── model/
   ├── repository/
   ├── service/
   ├── web/
   ├── exception/
   ├── config/
   └── KitchensinkApplication.java
   ```

### 1.2 Configure Database

1. Add database configuration in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:kitchensink
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.jpa.hibernate.ddl-auto=update
   spring.h2.console.enabled=true
   ```

2. Configure JPA properties:
   ```properties
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   ```

### 1.3 Setup Basic Application Class

Create the main Spring Boot application class:
```java
package org.jboss.as.quickstarts.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KitchensinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(KitchensinkApplication.class, args);
    }
}
```

## Phase 2: Core Application Migration

### 2.1 Domain Model Migration

1. Migrate the `Member` entity:
   - Keep JPA annotations (`@Entity`, `@Id`, `@GeneratedValue`, etc.)
   - Update import statements from `jakarta.persistence.*` to `javax.persistence.*` if using Spring Boot < 3.0, or keep as `jakarta.persistence.*` for Spring Boot 3.0+
   - Keep validation annotations, updating imports as needed
   - Replace `@XmlRootElement` with Jackson annotations if needed (`@JsonIgnoreProperties`, etc.)

2. Example migrated entity:
   ```java
   package org.jboss.as.quickstarts.kitchensink.model;

   import jakarta.persistence.Column;
   import jakarta.persistence.Entity;
   import jakarta.persistence.GeneratedValue;
   import jakarta.persistence.GenerationType;
   import jakarta.persistence.Id;
   import jakarta.persistence.Table;
   import jakarta.persistence.UniqueConstraint;
   import jakarta.validation.constraints.Digits;
   import jakarta.validation.constraints.Email;
   import jakarta.validation.constraints.NotEmpty;
   import jakarta.validation.constraints.NotNull;
   import jakarta.validation.constraints.Pattern;
   import jakarta.validation.constraints.Size;

   @Entity
   @Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
   public class Member {

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

       // Getters and setters
   }
   ```

### 2.2 Data Access Layer Migration

1. Replace `MemberRepository` with Spring Data JPA repository:
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

2. Migrate `MemberListProducer` to use Spring components:
   ```java
   package org.jboss.as.quickstarts.kitchensink.service;

   import org.jboss.as.quickstarts.kitchensink.model.Member;
   import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
   import org.springframework.stereotype.Component;

   import java.util.List;

   @Component
   public class MemberListProducer {

       private final MemberRepository memberRepository;

       public MemberListProducer(MemberRepository memberRepository) {
           this.memberRepository = memberRepository;
       }

       public List<Member> getMembers() {
           return memberRepository.findAllByOrderByNameAsc();
       }
   }
   ```

### 2.3 Service Layer Migration

1. Migrate `MemberRegistration` service:
   ```java
   package org.jboss.as.quickstarts.kitchensink.service;

   import org.jboss.as.quickstarts.kitchensink.model.Member;
   import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
   import org.springframework.context.ApplicationEventPublisher;
   import org.springframework.stereotype.Service;
   import org.springframework.transaction.annotation.Transactional;

   import java.util.logging.Logger;

   @Service
   public class MemberRegistration {

       private final Logger log;
       private final MemberRepository memberRepository;
       private final ApplicationEventPublisher eventPublisher;

       public MemberRegistration(Logger log, MemberRepository memberRepository, 
                                ApplicationEventPublisher eventPublisher) {
           this.log = log;
           this.memberRepository = memberRepository;
           this.eventPublisher = eventPublisher;
       }

       @Transactional
       public void register(Member member) throws Exception {
           log.info("Registering " + member.getName());
           memberRepository.save(member);
           eventPublisher.publishEvent(member);
       }
   }
   ```

2. Create a configuration for Logger injection:
   ```java
   package org.jboss.as.quickstarts.kitchensink.config;

   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;

   import java.util.logging.Logger;

   @Configuration
   public class LoggerConfig {

       @Bean
       public Logger produceLogger() {
           return Logger.getLogger(this.getClass().getName());
       }
   }
   ```

### 2.4 REST API Migration

1. Migrate `JaxRsActivator` to Spring Boot configuration:
   ```java
   package org.jboss.as.quickstarts.kitchensink.config;

   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.servlet.config.annotation.EnableWebMvc;

   @Configuration
   @EnableWebMvc
   public class WebConfig {
       // Additional web configuration if needed
   }
   ```

2. Migrate `MemberResourceRESTService` to Spring RestController:
   ```java
   package org.jboss.as.quickstarts.kitchensink.web;

   import jakarta.validation.Valid;
   import org.jboss.as.quickstarts.kitchensink.model.Member;
   import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
   import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
   import org.springframework.http.HttpStatus;
   import org.springframework.http.ResponseEntity;
   import org.springframework.validation.FieldError;
   import org.springframework.web.bind.MethodArgumentNotValidException;
   import org.springframework.web.bind.annotation.*;

   import java.util.HashMap;
   import java.util.List;
   import java.util.Map;
   import java.util.logging.Logger;

   @RestController
   @RequestMapping("/api/members")
   public class MemberController {

       private final Logger log;
       private final MemberRepository memberRepository;
       private final MemberRegistration registration;

       public MemberController(Logger log, MemberRepository memberRepository, 
                             MemberRegistration registration) {
           this.log = log;
           this.memberRepository = memberRepository;
           this.registration = registration;
       }

       @GetMapping
       public List<Member> listAllMembers() {
           return memberRepository.findAllByOrderByNameAsc();
       }

       @GetMapping("/{id}")
       public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {
           return memberRepository.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
       }

       @PostMapping
       public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
           try {
               // Check if email already exists
               if (emailAlreadyExists(member.getEmail())) {
                   Map<String, String> responseObj = new HashMap<>();
                   responseObj.put("email", "Email taken");
                   return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
               }

               registration.register(member);
               return ResponseEntity.ok().build();
           } catch (Exception e) {
               Map<String, String> responseObj = new HashMap<>();
               responseObj.put("error", e.getMessage());
               return ResponseEntity.badRequest().body(responseObj);
           }
       }

       @ExceptionHandler(MethodArgumentNotValidException.class)
       public ResponseEntity<Map<String, String>> handleValidationExceptions(
               MethodArgumentNotValidException ex) {
           Map<String, String> errors = new HashMap<>();
           ex.getBindingResult().getAllErrors().forEach((error) -> {
               String fieldName = ((FieldError) error).getField();
               String errorMessage = error.getDefaultMessage();
               errors.put(fieldName, errorMessage);
           });
           return ResponseEntity.badRequest().body(errors);
       }

       private boolean emailAlreadyExists(String email) {
           return memberRepository.findByEmail(email).isPresent();
       }
   }
   ```

### 2.5 Exception Handling

Create a global exception handler:
```java
package org.jboss.as.quickstarts.kitchensink.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Resource not found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### 2.6 Application Properties

Create comprehensive application properties:
```properties
# Server configuration
server.port=8080
server.servlet.context-path=/kitchensink

# Database configuration
spring.datasource.url=jdbc:h2:mem:kitchensink
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# JPA/Hibernate configuration
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.org.jboss.as.quickstarts=INFO

# Jackson configuration
spring.jackson.serialization.fail-on-empty-beans=false
```

## Phase 3: Testing and Validation

### 3.1 Unit Testing

1. Create unit tests for repositories:
   ```java
   package org.jboss.as.quickstarts.kitchensink.repository;

   import org.jboss.as.quickstarts.kitchensink.model.Member;
   import org.junit.jupiter.api.Test;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

   import java.util.List;
   import java.util.Optional;

   import static org.assertj.core.api.Assertions.assertThat;

   @DataJpaTest
   public class MemberRepositoryTest {

       @Autowired
       private MemberRepository memberRepository;

       @Test
       public void testFindByEmail() {
           // Given
           Member member = new Member();
           member.setName("John Doe");
           member.setEmail("john@example.com");
           member.setPhoneNumber("1234567890");
           memberRepository.save(member);

           // When
           Optional<Member> found = memberRepository.findByEmail("john@example.com");

           // Then
           assertThat(found).isPresent();
           assertThat(found.get().getName()).isEqualTo("John Doe");
       }

       @Test
       public void testFindAllByOrderByNameAsc() {
           // Given
           Member member1 = new Member();
           member1.setName("John Doe");
           member1.setEmail("john@example.com");
           member1.setPhoneNumber("1234567890");

           Member member2 = new Member();
           member2.setName("Alice Smith");
           member2.setEmail("alice@example.com");
           member2.setPhoneNumber("0987654321");

           memberRepository.save(member1);
           memberRepository.save(member2);

           // When
           List<Member> members = memberRepository.findAllByOrderByNameAsc();

           // Then
           assertThat(members).hasSize(2);
           assertThat(members.get(0).getName()).isEqualTo("Alice Smith");
           assertThat(members.get(1).getName()).isEqualTo("John Doe");
       }
   }
   ```

2. Create unit tests for services:
   ```java
   package org.jboss.as.quickstarts.kitchensink.service;

   import org.jboss.as.quickstarts.kitchensink.model.Member;
   import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
   import org.junit.jupiter.api.BeforeEach;
   import org.junit.jupiter.api.Test;
   import org.junit.jupiter.api.extension.ExtendWith;
   import org.mockito.Mock;
   import org.mockito.junit.jupiter.MockitoExtension;
   import org.springframework.context.ApplicationEventPublisher;

   import java.util.logging.Logger;

   import static org.mockito.ArgumentMatchers.any;
   import static org.mockito.Mockito.verify;
   import static org.mockito.Mockito.when;

   @ExtendWith(MockitoExtension.class)
   public class MemberRegistrationTest {

       @Mock
       private MemberRepository memberRepository;

       @Mock
       private Logger log;

       @Mock
       private ApplicationEventPublisher eventPublisher;

       private MemberRegistration memberRegistration;

       @BeforeEach
       public void setUp() {
           memberRegistration = new MemberRegistration(log, memberRepository, eventPublisher);
       }

       @Test
       public void testRegister() throws Exception {
           // Given
           Member member = new Member();
           member.setName("John Doe");
           member.setEmail("john@example.com");
           member.setPhoneNumber("1234567890");

           when(memberRepository.save(any(Member.class))).thenReturn(member);

           // When
           memberRegistration.register(member);

           // Then
           verify(memberRepository).save(member);
           verify(eventPublisher).publishEvent(member);
       }
   }
   ```

### 3.2 Integration Testing

1. Create integration tests for REST controllers:
   ```java
   package org.jboss.as.quickstarts.kitchensink.web;

   import com.fasterxml.jackson.databind.ObjectMapper;
   import org.jboss.as.quickstarts.kitchensink.model.Member;
   import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
   import org.junit.jupiter.api.Test;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.http.MediaType;
   import org.springframework.test.web.servlet.MockMvc;
   import org.springframework.transaction.annotation.Transactional;

   import static org.hamcrest.Matchers.hasSize;
   import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
   import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
   import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
   import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

   @SpringBootTest
   @AutoConfigureMockMvc
   @Transactional
   public class MemberControllerIT {

       @Autowired
       private MockMvc mockMvc;

       @Autowired
       private ObjectMapper objectMapper;

       @Autowired
       private MemberRepository memberRepository;

       @Test
       public void testListAllMembers() throws Exception {
           // Given
           Member member = new Member();
           member.setName("John Doe");
           member.setEmail("john@example.com");
           member.setPhoneNumber("1234567890");
           memberRepository.save(member);

           // When/Then
           mockMvc.perform(get("/api/members"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", hasSize(1)))
                   .andExpect(jsonPath("$[0].name").value("John Doe"));
       }

       @Test
       public void testCreateMember() throws Exception {
           // Given
           Member member = new Member();
           member.setName("Jane Doe");
           member.setEmail("jane@example.com");
           member.setPhoneNumber("1234567890");

           // When/Then
           mockMvc.perform(post("/api/members")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(member)))
                   .andExpect(status().isOk());
       }

       @Test
       public void testCreateMemberWithDuplicateEmail() throws Exception {
           // Given
           Member existingMember = new Member();
           existingMember.setName("John Doe");
           existingMember.setEmail("john@example.com");
           existingMember.setPhoneNumber("1234567890");
           memberRepository.save(existingMember);

           Member newMember = new Member();
           newMember.setName("Jane Doe");
           newMember.setEmail("john@example.com");  // Same email
           newMember.setPhoneNumber("0987654321");

           // When/Then
           mockMvc.perform(post("/api/members")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(newMember)))
                   .andExpect(status().isConflict())
                   .andExpect(jsonPath("$.email").value("Email taken"));
       }
   }
   ```

### 3.3 End-to-End Testing

1. Create end-to-end tests with tools like Selenium or Cypress (if UI is migrated)
2. Test complete flows from user registration to data retrieval

### 3.4 Performance Testing

1. Conduct load testing with tools like JMeter or Gatling
2. Compare performance metrics with the original application

## Phase 4: Deployment and Documentation

### 4.1 Containerization

1. Create a Dockerfile:
   ```dockerfile
   FROM eclipse-temurin:17-jdk-alpine
   VOLUME /tmp
   COPY target/*.jar app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

2. Create Docker Compose configuration for development:
   ```yaml
   version: '3.8'
   services:
     app:
       build: .
       ports:
         - "8080:8080"
       environment:
         - SPRING_PROFILES_ACTIVE=dev
       depends_on:
         - db
     db:
       image: postgres:14-alpine
       environment:
         - POSTGRES_USER=postgres
         - POSTGRES_PASSWORD=postgres
         - POSTGRES_DB=kitchensink
       ports:
         - "5432:5432"
       volumes:
         - postgres-data:/var/lib/postgresql/data
   volumes:
     postgres-data:
   ```

### 4.2 CI/CD Pipeline

1. Create a CI/CD pipeline configuration (e.g., GitHub Actions, Jenkins)
2. Include build, test, and deployment stages

### 4.3 Documentation

1. Update README with setup and usage instructions
2. Document API endpoints using Swagger/OpenAPI
3. Create developer documentation for future maintenance

## Timeline Estimate

| Phase | Tasks | Estimated Duration |
|-------|-------|-------------------|
| Phase 1: Project Setup | Create project structure, configure dependencies | 1-2 days |
| Phase 2: Core Application Migration | Domain model, repositories, services, REST API | 5-7 days |
| Phase 3: Testing and Validation | Unit tests, integration tests, end-to-end tests | 3-5 days |
| Phase 4: Deployment and Documentation | Containerization, CI/CD, documentation | 2-3 days |
| **Total** | | **11-17 days** |

Note: This timeline assumes a single developer working full-time on the migration. The timeline can be adjusted based on team size and familiarity with Spring Boot.

## Potential Challenges and Mitigation

| Challenge | Description | Mitigation Strategy |
|-----------|-------------|---------------------|
| CDI to Spring DI differences | Subtle differences in how dependency injection works | Carefully review all injection points and test thoroughly |
| Transaction management | EJB vs. Spring transaction behavior differences | Use `@Transactional` with appropriate propagation settings |
| JAX-RS to Spring MVC | Different annotations and behavior | Map each endpoint carefully and test thoroughly |
| Bean validation differences | Potential differences in validation behavior | Verify all validation rules work the same way |
| Database schema evolution | Changes in how JPA/Hibernate manages schema | Use Flyway or Liquibase for schema management |
| Testing approach differences | Different testing frameworks and approaches | Create comprehensive test suite with Spring Test |
| UI integration | If JSF is used, migration to a Spring-compatible UI | Consider Thymeleaf as a server-side alternative or a modern frontend framework |

## Resources and References

1. [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
2. [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
3. [Spring MVC Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)
4. [Jakarta EE to Spring Boot Migration Guide](https://www.baeldung.com/java-ee-vs-spring)
5. [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
