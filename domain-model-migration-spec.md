
# Domain Model Migration Specification: JBoss EAP to Spring Boot

## Table of Contents
- [Overview](#overview)
- [Entity Mapping](#entity-mapping)
- [Annotation Changes](#annotation-changes)
- [Class Diagram](#class-diagram)
- [Code Examples](#code-examples)
- [Validation Strategy](#validation-strategy)
- [Serialization Changes](#serialization-changes)
- [Testing Strategy](#testing-strategy)
- [Migration Checklist](#migration-checklist)

## Overview

This document provides a detailed specification for migrating the domain model components from the JBoss EAP 'kitchensink' application to Spring Boot. The domain model consists primarily of the `Member` entity, which represents a registered user with basic contact information.

### Current Architecture

In the JBoss EAP application, the domain model:
- Uses Jakarta Persistence (JPA) for object-relational mapping
- Uses Jakarta Bean Validation for data validation
- Uses JAXB (`@XmlRootElement`) for XML serialization
- Follows the JPA entity pattern with getters and setters

### Target Architecture

In the Spring Boot application, the domain model will:
- Use Spring Data JPA for object-relational mapping
- Use Spring Boot's validation (based on Jakarta Bean Validation)
- Use Jackson for JSON serialization
- Follow the same JPA entity pattern, with potential enhancements

## Entity Mapping

The following table shows the mapping between the JBoss EAP entity and its Spring Boot equivalent:

| JBoss EAP Entity | Spring Boot Entity | Migration Complexity |
|------------------|-------------------|----------------------|
| Member | Member | Low |

### Field Mapping

| Field | JBoss EAP Type | Spring Boot Type | Changes Required |
|-------|---------------|-----------------|------------------|
| id | Long | Long | None |
| name | String | String | None |
| email | String | String | None |
| phoneNumber | String | String | None |

## Annotation Changes

### JPA Annotations

| JBoss EAP Annotation | Spring Boot Annotation | Notes |
|----------------------|------------------------|-------|
| `@Entity` | `@Entity` | No change needed |
| `@Id` | `@Id` | No change needed |
| `@GeneratedValue` | `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Consider specifying the strategy explicitly |
| `@Column` | `@Column` | No change needed |
| `@Table` | `@Table` | No change needed |
| `@UniqueConstraint` | `@UniqueConstraint` | No change needed |

### Validation Annotations

| JBoss EAP Annotation | Spring Boot Annotation | Notes |
|----------------------|------------------------|-------|
| `@NotNull` | `@NotNull` | No change needed |
| `@Size` | `@Size` | No change needed |
| `@Pattern` | `@Pattern` | No change needed |
| `@Email` | `@Email` | No change needed |
| `@NotEmpty` | `@NotEmpty` | No change needed |
| `@Digits` | `@Digits` | No change needed |

### Serialization Annotations

| JBoss EAP Annotation | Spring Boot Annotation | Notes |
|----------------------|------------------------|-------|
| `@XmlRootElement` | Remove or replace with Jackson annotations | Spring Boot uses Jackson for JSON serialization by default |
| N/A | `@JsonIgnoreProperties(ignoreUnknown = true)` | Add to handle unknown properties during deserialization |

### Import Statement Changes

| JBoss EAP Import | Spring Boot Import | Notes |
|------------------|-------------------|-------|
| `jakarta.persistence.*` | `jakarta.persistence.*` (Spring Boot 3.x) or `javax.persistence.*` (Spring Boot 2.x) | Depends on Spring Boot version |
| `jakarta.validation.constraints.*` | `jakarta.validation.constraints.*` (Spring Boot 3.x) or `javax.validation.constraints.*` (Spring Boot 2.x) | Depends on Spring Boot version |
| `jakarta.xml.bind.annotation.XmlRootElement` | `com.fasterxml.jackson.annotation.*` | Replace with Jackson annotations |

## Class Diagram

```
+-------------------+
|      Member       |
+-------------------+
| -id: Long         |
| -name: String     |
| -email: String    |
| -phoneNumber: String |
+-------------------+
| +getId(): Long    |
| +setId(Long)      |
| +getName(): String|
| +setName(String)  |
| +getEmail(): String|
| +setEmail(String) |
| +getPhoneNumber(): String|
| +setPhoneNumber(String)|
+-------------------+
```

## Code Examples

### Original JBoss EAP Member Entity

```java
package org.jboss.as.quickstarts.kitchensink.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@SuppressWarnings("serial")
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
```

### Migrated Spring Boot Member Entity

```java
package org.jboss.as.quickstarts.kitchensink.model;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    // Optional: Add toString method for better logging
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
```

### Optional Lombok Enhancement

If using Lombok to reduce boilerplate:

```java
package org.jboss.as.quickstarts.kitchensink.model;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

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

## Validation Strategy

Spring Boot uses the same validation API as Jakarta EE, so most validation annotations can be used without changes. However, there are some differences in how validation is triggered:

1. **JBoss EAP**: Validation is often triggered automatically by the CDI container and the EJB container.
2. **Spring Boot**: Validation needs to be explicitly triggered in controllers using the `@Valid` annotation on method parameters.

Example of validation in a Spring Boot controller:

```java
@RestController
@RequestMapping("/api/members")
public class MemberController {

    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        // Implementation
    }
}
```

## Serialization Changes

The JBoss EAP application uses JAXB (`@XmlRootElement`) for XML serialization. Spring Boot uses Jackson for JSON serialization by default. The migration involves:

1. Removing JAXB annotations
2. Adding Jackson annotations as needed
3. Configuring Jackson in Spring Boot

Example of Jackson configuration in `application.properties`:

```properties
spring.jackson.serialization.fail-on-empty-beans=false
spring.jackson.deserialization.fail-on-unknown-properties=false
spring.jackson.default-property-inclusion=non_null
```

## Testing Strategy

Testing the domain model in Spring Boot involves:

1. **Unit Testing**: Test entity validation and behavior
2. **Repository Testing**: Test JPA repository methods with `@DataJpaTest`
3. **Integration Testing**: Test the entity in the context of the full application

Example of a unit test for the Member entity:

```java
package org.jboss.as.quickstarts.kitchensink.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidMember() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmail() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("invalid-email");
        member.setPhoneNumber("1234567890");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }
}
```

## Migration Checklist

Use this checklist to ensure a complete domain model migration:

- [ ] Update import statements to use the correct package (jakarta.* for Spring Boot 3.x, javax.* for Spring Boot 2.x)
- [ ] Replace `@XmlRootElement` with Jackson annotations
- [ ] Specify the generation strategy for `@GeneratedValue`
- [ ] Add `serialVersionUID` for better serialization control
- [ ] Add helpful methods like `toString()`, `equals()`, and `hashCode()` or use Lombok
- [ ] Create unit tests for entity validation
- [ ] Configure Jackson in `application.properties`
- [ ] Ensure validation is properly triggered in controllers with `@Valid`
- [ ] Check for any custom validation logic that might need to be migrated
- [ ] Verify database column names and types match expectations
- [ ] Consider adding database indexes for frequently queried fields
- [ ] Update any named queries or JPQL statements
- [ ] Migrate any entity listeners or lifecycle callbacks
