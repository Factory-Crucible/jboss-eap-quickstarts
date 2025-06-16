# Kitchensink – Spring Boot Migration

This project is the Spring Boot 3.x rewrite of the classic **JBoss EAP “kitchensink”** example.  
Phase 1 (project setup) is complete, and **Phase 2** has begun.  
This README captures the work finished so far – the **Domain Model** and **Data Access Layer** – and explains how to run and extend the new codebase.

---

## 1  Migration Goals

| Original JBoss Component | Spring Boot Equivalent | Status |
|--------------------------|------------------------|--------|
| `Member` JPA Entity      | `Member` Lombok-powered Entity | ✅ done |
| `MemberRepository` (CDI + `EntityManager`) | `MemberRepository` (Spring Data JPA) | ✅ done |
| EJB Stateless Bean (`MemberRegistration`) | Spring `@Service` | ⏳ next step |
| JAX-RS `MemberResourceRESTService` | Spring `@RestController` | ⏳ next step |
| CDI / Arquillian plumbing | Spring Boot autoconfiguration | ✅ n/a |
| Bean Validation | Jakarta Validation (unchanged) | ✅ preserved |

---

## 2  Project Structure

```
kitchensink-spring-boot/
├── src/main/java
│   └── org/jboss/as/quickstarts/kitchensink
│       ├── KitchensinkApplication.java   # Spring Boot entry point
│       ├── model
│       │   └── Member.java               # Domain entity (migrated)
│       └── repository
│           └── MemberRepository.java     # Spring Data interface
├── src/main/resources
│   └── application.properties            # H2 + JPA config
└── pom.xml                               # Spring Boot 3 + Java 21
```

Key decisions:

* **Package name** remains under `org.jboss.as.quickstarts.kitchensink` to ease code moves and diffing.
* **Lombok** (`@Data`, `@Builder`, etc.) removes boilerplate getters/setters.
* **Spring Data JPA** supplies CRUD and finder queries without manual `EntityManager` code.

---

## 3  Running the Application Locally

```bash
cd kitchensink-spring-boot
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080/kitchensink`.

* **H2 Console** – `http://localhost:8080/kitchensink/h2-console` (JDBC URL: `jdbc:h2:mem:kitchensinkdb`).

Because only persistence components exist today, there are no REST endpoints yet; unit tests (to be added) will exercise the repository.

---

## 4  Domain Model Highlights

```java
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member { … }
```

* Validation constraints (`@Email`, `@Pattern`, etc.) retained one-for-one.
* Primary key strategy switched to `GenerationType.IDENTITY` for H2/MySQL parity.
* Serializable for session clustering parity with the original.

---

## 5  Data Access Layer Highlights

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    List<Member> findAllByOrderByNameAsc();
}
```

* Extends `JpaRepository` → gains paging, sorting, and CRUD for free.
* Custom finder names replace previously hand-written Criteria API queries.

---

## 6  Next Steps (Phase 2)

1. **Business Logic** – migrate `MemberRegistration` to a Spring `@Service` with `@Transactional`.
2. **REST API** – implement `MemberController` mirroring JAX-RS endpoints.
3. **Global Exception Handling** – `@ControllerAdvice` mapping validation and constraint violations.
4. **Integration Tests** – Spring Boot Test + Testcontainers (H2 now, Postgres later).

---

## 7  Contributing / Feedback

Open a PR or create a Linear ticket referencing **PRO-339** for any issues.  
We follow Factory Engineering conventions – see `docs/platform-eng-conventions.md` in the monorepo.

Happy coding! 🚀
