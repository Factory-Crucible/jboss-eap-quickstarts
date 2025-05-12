# Data Access Layer Migration Specification: JBoss EAP to Spring Boot

## Table of Contents
- [Overview](#overview)
- [Repository Mapping](#repository-mapping)
- [Query Method Conversion](#query-method-conversion)
- [Transaction Management](#transaction-management)
- [Dependency Injection Changes](#dependency-injection-changes)
- [Code Examples](#code-examples)
- [Testing Strategy](#testing-strategy)
- [Performance Considerations](#performance-considerations)
- [Migration Checklist](#migration-checklist)

## Overview

This document provides a detailed specification for migrating the data access layer components from the JBoss EAP 'kitchensink' application to Spring Boot. The data access layer is responsible for interacting with the database and providing CRUD operations for the domain entities.

### Current Architecture

In the JBoss EAP application, the data access layer:
- Uses JPA with direct EntityManager operations
- Uses CDI for dependency injection (`@Inject`)
- Uses JPA Criteria API for dynamic queries
- Relies on container-managed transactions (via EJB)
- Is implemented as a concrete class with injected dependencies

### Target Architecture

In the Spring Boot application, the data access layer will:
- Use Spring Data JPA repositories
- Use Spring's dependency injection
- Use Spring Data's query methods and derived queries
- Use Spring's transaction management
- Be implemented as interfaces extending Spring Data JPA repositories

## Repository Mapping

The following table shows the mapping between the JBoss EAP repository and its Spring Boot equivalent:

| JBoss EAP Component | Spring Boot Component | Migration Complexity |
|---------------------|----------------------|----------------------|
| MemberRepository (class) | MemberRepository (interface) | Medium |

## Query Method Conversion

### EntityManager Operations to Spring Data Repository Methods

| JBoss EAP EntityManager Operation | Spring Data Repository Method | Notes |
|-----------------------------------|------------------------------|-------|
| `em.find(Member.class, id)` | `repository.findById(id)` | Returns `Optional<Member>` in Spring Data |
| `em.persist(entity)` | `repository.save(entity)` | Used for both create and update |
| `em.merge(entity)` | `repository.save(entity)` | Spring Data uses the same method for persist and merge |
| `em.remove(entity)` | `repository.delete(entity)` | |
| `em.createQuery(criteria).getResultList()` | `repository.findAll()` or custom query methods | Spring Data provides built-in methods for common queries |
| `em.createQuery(criteria).getSingleResult()` | `repository.findBy...()` | Returns a single entity or throws exception |

### JPA Criteria API to Spring Data Query Methods

| JBoss EAP Criteria Query | Spring Data Query Method | Notes |
|--------------------------|--------------------------|-------|
| `criteria.select(member).where(cb.equal(member.get("email"), email))` | `findByEmail(String email)` | Spring Data derives the query from the method name |
| `criteria.select(member).orderBy(cb.asc(member.get("name")))` | `findAllByOrderByNameAsc()` | Spring Data supports ordering in method names |

### Custom Query Methods

For more complex queries that cannot be expressed through method names, Spring Data provides several options:

1. **@Query Annotation**: Define JPQL or SQL queries directly on repository methods
   ```java
   @Query("SELECT m FROM Member m WHERE m.email LIKE %:pattern%")
   List<Member> findMembersWithEmailLike(@Param("pattern") String pattern);
   ```

2. **Specification API**: For dynamic queries similar to Criteria API
   ```java
   public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
       // Standard methods plus support for Specifications
   }
   ```

3. **QueryDSL Integration**: For type-safe queries
   ```java
   public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
       // Standard methods plus support for Querydsl predicates
   }
   ```

## Transaction Management

### JBoss EAP Transaction Management

In the JBoss EAP application:
- Transactions are managed by the EJB container via `@Stateless` beans
- Transaction boundaries are typically at the service layer
- No explicit transaction annotations are needed in most cases

### Spring Boot Transaction Management

In the Spring Boot application:
- Transactions are managed by Spring's transaction manager
- `@Transactional` annotation is used to define transaction boundaries
- Repository methods are transactional by default
- Service methods should be explicitly annotated with `@Transactional`

Example of transaction configuration in a service:
```java
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void saveMember(Member member) {
        memberRepository.save(member);
    }
}
```

## Dependency Injection Changes

### JBoss EAP Dependency Injection

In the JBoss EAP application:
- CDI is used for dependency injection
- `@Inject` annotation is used to inject dependencies
- `@ApplicationScoped` is used for repository scope

```java
@ApplicationScoped
public class MemberRepository {

    @Inject
    private EntityManager em;
    
    // Repository methods
}
```

### Spring Boot Dependency Injection

In the Spring Boot application:
- Spring's dependency injection is used
- Constructor injection is preferred over field injection
- Repository interfaces are automatically implemented by Spring Data

```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Query methods
}
```

## Code Examples

### Original JBoss EAP MemberRepository

```java
package org.jboss.as.quickstarts.kitchensink.data;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

import org.jboss.as.quickstarts.kitchensink.model.Member;

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
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(member).where(cb.equal(member.get("email"), email));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Member> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
        Root<Member> member = criteria.from(Member.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(member).orderBy(cb.asc(member.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
```

### Migrated Spring Boot MemberRepository

```java
package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find a member by email address
     * 
     * @param email the email address to search for
     * @return the member if found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find all members ordered by name ascending
     * 
     * @return list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
}
```

### Advanced Spring Data Repository Example

For more complex scenarios, you might need additional query methods or custom implementations:

```java
package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    
    Optional<Member> findByEmail(String email);
    
    List<Member> findAllByOrderByNameAsc();
    
    @Query("SELECT m FROM Member m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Member> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
    
    List<Member> findByPhoneNumberStartingWith(String areaCode);
    
    @Query(value = "SELECT * FROM Member m WHERE m.registration_date > :date ORDER BY m.name", nativeQuery = true)
    List<Member> findRecentMembersNative(@Param("date") java.sql.Date date);
}
```

## Testing Strategy

Testing the data access layer in Spring Boot involves:

1. **Repository Unit Tests**: Test repository methods in isolation using `@DataJpaTest`
2. **Integration Tests**: Test repositories in the context of the full application

### Repository Unit Test Example

```java
package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testFindByEmail() {
        // Given
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPhoneNumber("1234567890");
        entityManager.persist(member);
        entityManager.flush();

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
        entityManager.persist(member1);

        Member member2 = new Member();
        member2.setName("Alice Smith");
        member2.setEmail("alice@example.com");
        member2.setPhoneNumber("0987654321");
        entityManager.persist(member2);
        entityManager.flush();

        // When
        List<Member> members = memberRepository.findAllByOrderByNameAsc();

        // Then
        assertThat(members).hasSize(2);
        assertThat(members.get(0).getName()).isEqualTo("Alice Smith");
        assertThat(members.get(1).getName()).isEqualTo("John Doe");
    }
}
```

## Performance Considerations

When migrating from JPA EntityManager to Spring Data repositories, consider the following performance aspects:

1. **N+1 Query Problem**: Spring Data can generate inefficient queries for relationships. Use `@EntityGraph` to optimize:
   ```java
   @EntityGraph(attributePaths = {"addresses", "orders"})
   List<Member> findAllWithAddressesAndOrders();
   ```

2. **Pagination**: For large result sets, use pagination:
   ```java
   Page<Member> findByLastName(String lastName, Pageable pageable);
   ```

3. **Batch Operations**: For bulk operations, consider using batch methods:
   ```java
   @Modifying
   @Query("UPDATE Member m SET m.active = false WHERE m.lastLogin < :date")
   int deactivateInactiveMembers(@Param("date") LocalDate date);
   ```

4. **Caching**: Consider adding caching for frequently accessed data:
   ```java
   @Cacheable("members")
   Optional<Member> findByEmail(String email);
   ```

## Migration Checklist

Use this checklist to ensure a complete data access layer migration:

- [ ] Create Spring Data repository interfaces for each JBoss repository class
- [ ] Map all EntityManager query methods to Spring Data query methods
- [ ] Update service classes to use the new repository interfaces
- [ ] Add appropriate transaction annotations to service methods
- [ ] Create unit tests for all repository methods
- [ ] Verify query performance and optimize as needed
- [ ] Update any custom query logic to use Spring Data's mechanisms
- [ ] Configure appropriate transaction isolation levels
- [ ] Add database connection pooling configuration
- [ ] Consider adding caching for frequently accessed data
- [ ] Update any code that directly used EntityManager to use repositories instead
- [ ] Verify that all exception handling is appropriate for Spring Data
- [ ] Add appropriate logging for database operations
- [ ] Consider adding auditing with Spring Data JPA Auditing
