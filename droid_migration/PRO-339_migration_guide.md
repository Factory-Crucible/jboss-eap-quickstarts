# PRO-339 – Core Application Migration Guide  
_Migrating the “kitchensink” example from **JBoss EAP (Java EE)** to **Spring Boot 3 + MySQL**_

---

## Table of Contents
1. Purpose & Scope  
2. Prerequisites  
3. Migration Overview  
4. Step-by-Step Migration Instructions  
   4.1  Bootstrap Spring Boot Project  
   4.2  Domain Model Migration  
   4.3  Data-Access Migration  
   4.4  Business Logic & Services  
   4.5  REST Layer Migration  
   4.6  Validation & Exception Handling  
   4.7  Persistence Configuration (MySQL)  
   4.8  API Documentation with SpringDoc  
   4.9  Testing Strategy  
5. Key Architectural Decisions  
6. Troubleshooting & Gotchas  
7. Next Steps  

---

## 1  Purpose & Scope
This guide documents **Phase 2 (Core Application)** of ticket **PRO-339**.  
It explains how we moved the legacy *jboss-eap-quickstarts/kitchensink* example to a modern Spring Boot 3 stack, focusing on:

* Domain entities  
* Repository / DAO layer  
* Business services  
* RESTful API  
* Validation & error handling  
* MySQL configuration & data access  
* Automated testing & OpenAPI docs  

Frontend (JSF) migration, CI/CD, security and production ops are **out of scope** and handled in later phases.

---

## 2  Prerequisites
| Tool | Version | Notes |
|------|---------|-------|
| JDK | 21 | LTS baseline required by Spring Boot 3 |
| Maven | 3.9 (wrapper provided) | or Gradle 8 if preferred |
| Docker Desktop | 4.x | for running local MySQL/Testcontainers |
| Git | latest | pull branch `droid/spring-boot-migration` |

---

## 3  Migration Overview
Legacy stack → Target stack mapping:

| Legacy (JBoss EAP) | Spring Boot 3 |
|--------------------|---------------|
| `@Stateless` EJB | `@Service` POJO |
| CDI `@Inject` | Constructor injection |
| JAX-RS `@Path`, `@GET` | `@RestController`, `@GetMapping` |
| `EntityManager` DAO | Spring Data `JpaRepository` |
| `persistence.xml` | `application.properties` |
| Bean Validation (manual) | Auto-triggered via `@Valid` |
| XML configs | Convention & properties |

Result: single executable **JAR** or container image, embedded Tomcat, no external app server.

---

## 4  Step-by-Step Migration Instructions

### 4.1  Bootstrap Spring Boot Project
1. Run Spring Initializr or use the provided **pom.xml**.  
   Dependencies: `spring-boot-starter-web`, `data-jpa`, `validation`, `lombok`, `mysql-connector-j`, `springdoc-openapi`.  
2. Commit skeleton under `kitchensink-spring-boot/`.

### 4.2  Domain Model Migration
* **Copy** `Member.java` → `com.example.kitchensink.model.Member`
* Keep JPA & Jakarta Validation annotations.
* Add Lombok:  
  `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
* Retain unique constraint on `email`.

### 4.3  Data-Access Migration
* Create `MemberRepository extends JpaRepository<Member,Long>`.
* Methods:  
  `Optional<Member> findByEmail(String email);`  
  `@Query("select m from Member m order by m.name") List<Member> findAllOrderedByName();`
* **Why**: eliminates boiler-plate DAO & `EntityManager` code.

### 4.4  Business Logic & Services
* Create `MemberService` with `@Service` + `@RequiredArgsConstructor`.
* Responsibilities:
  * Validate entity (`Validator`)  
  * Enforce email uniqueness (`DuplicateEmailException`)  
  * Wrap DB ops in `@Transactional`.
* **Decision**: business rules live in service layer, not controller.

### 4.5  REST Layer Migration
* Replace JAX-RS classes with `MemberController` (`@RestController`).
* Routes:
  * `GET  /api/members`
  * `GET  /api/members/{id}`
  * `POST /api/members` → returns **201 Created** + `Location` header.
* Automatic JSON (Jackson) serialization—no `Response` wrappers needed.

### 4.6  Validation & Exception Handling
* Create `GlobalExceptionHandler` (`@RestControllerAdvice`) mapping:
  * 400 – `MethodArgumentNotValidException`, `ConstraintViolationException`
  * 409 – `DuplicateEmailException`, `DataIntegrityViolationException`
  * 404 – `EntityNotFoundException`
  * 500 – generic
* Unified `ErrorResponse` DTO (timestamp, status, message, field map).

### 4.7  Persistence Configuration (MySQL)
* `application.properties`:
```
spring.datasource.url=jdbc:mysql://localhost:3306/kitchensink?…  
spring.datasource.username=root  
spring.datasource.password=change-me  
spring.jpa.hibernate.ddl-auto=update
```
* Local DB via `docker-compose.yml` (MySQL 8 with health-check).
* **Note**: tests still use H2 or Testcontainers-MySQL.

### 4.8  API Documentation with SpringDoc
* Add `springdoc-openapi-starter-webmvc-ui`.
* `OpenApiConfig` customises title, version & servers.
* Swagger UI available at `/swagger-ui.html`.

### 4.9  Testing Strategy
| Layer | Tooling | Highlights |
|-------|---------|------------|
| Unit | JUnit 5 + Mockito | `MemberServiceTest` |
| Web | Spring MockMvc | `MemberControllerTest` |
| Integration | Testcontainers-MySQL | `MemberRepositoryIT` |

Run all via `./mvnw verify`.

---

## 5  Key Architectural Decisions

| Decision | Rationale |
|----------|-----------|
| **Spring Data JPA** over raw `EntityManager` | Auto CRUD, less boiler-plate, paging/sorting out-of-box. |
| **Constructor injection** | Immutability, easier testing. |
| **Centralised exception handling** | Consistent error payloads, thin controllers. |
| **MySQL dev DB** instead of H2 | Early parity with prod, catches dialect issues. |
| **Lombok** | 40-50 % fewer boiler-plate lines. |
| **Testcontainers** | Real DB integration without env setup. |
| **OpenAPI first-class** | Self-documenting API, accelerates client integration. |

---

## 6  Troubleshooting & Gotchas
| Issue | Fix |
|-------|-----|
| `Cannot connect to MySQL` | Ensure compose container is **healthy** & port 3306 free. |
| Duplicate email persists | Confirm unique index created or `ddl-auto=update`. |
| Bean Validation not triggered | Add `@Valid` on `@RequestBody` and enable Spring Boot starter validation. |
| Lazy-loading exception in JSON | Disable Open Session in View (`spring.jpa.open-in-view=false`) and fetch eagerly or DTO-map. |
| Tests slow on ARM Macs | Pin Ryuk image (`testcontainers.properties`). |

---

## 7  Next Steps
1. **Flyway/Liquibase** for controlled schema evolution.  
2. **Spring Security + JWT** for auth.  
3. Replace JSF UI with modern SPA or Thymeleaf.  
4. CI/CD pipeline (GitHub Actions) to build image & deploy to Kubernetes.  

---

> **Outcome:** The core application now runs as a single Spring Boot service (`java ‑jar …`) with the same functional behaviour, faster startup, clearer structure and full test coverage.  
