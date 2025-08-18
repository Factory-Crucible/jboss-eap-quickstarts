# Kitchensink Spring Boot Migration

## 1 – Project Overview & Migration Purpose
This module is the **Spring Boot 3** re-implementation of the classic _JBoss EAP “kitchensink”_ demo.  
The goal of the migration is to modernise the codebase while preserving functional parity:

* **Self-contained executable JAR** instead of a WAR deployed to an application server.  
* **Spring Boot conventions** replace Jakarta EE XML / descriptors for faster developer onboarding.  
* Codebase ready for cloud-native deployment (Docker/Kubernetes) and CI pipelines.

## 2 – Technology Stack
| Layer | Technology | Version |
|-------|------------|---------|
| Runtime | Spring Boot | **3.2.1** |
| Language | Java | **21** |
| Persistence | Spring Data JPA, Hibernate | 6.x (managed by Spring Boot) |
| Validation | Jakarta Bean Validation | 3.0 (via `spring-boot-starter-validation`) |
| Database (dev/test) | H2 in-memory | 2.x |
| Build Tool | Maven | 3.9+ |
| Lombok | 1.18.x |

### Key Spring Boot Starters
`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-test`, `spring-boot-devtools`

## 3 – Jakarta EE → Spring Boot Mapping

| Jakarta EE component | Original class | Spring Boot replacement |
|----------------------|---------------|-------------------------|
| CDI `@EntityManager` injection | `MemberRepository.java` | Spring Data `JpaRepository` |
| CDI `@ApplicationScoped` bean | `MemberRepository` | Spring stereotype eliminated – interface only |
| EJB `@Stateless` service | `MemberRegistration` | `MemberService` with `@Service` + `@Transactional` |
| JAX-RS `@Path` resources | `MemberResourceRESTService` | `MemberController` with `@RestController` |
| `persistence.xml` | XML configuration | Properties in `application.properties` |
| Bean Validation | Same annotations | Same (no changes required) |
| Arquillian tests | Integration tests | Spring Boot Test + TestRestTemplate |

## 4 – Architecture Overview
```
┌───────────────────────────┐
│  Controller Layer (REST)  │  → `MemberController`
└─────────────┬─────────────┘
              ↓
┌───────────────────────────┐
│   Service Layer (Biz)     │  → `MemberService`
└─────────────┬─────────────┘
              ↓
┌───────────────────────────┐
│ Repository Layer (JPA)    │  → `MemberRepository`
└─────────────┬─────────────┘
              ↓
┌───────────────────────────┐
│     Database (H2)         │
└───────────────────────────┘
```
*Global exception handling* is centralised in `GlobalExceptionHandler`.  
DTOs are not required; the domain entity is sent directly for simplicity in the demo.

## 5 – API Endpoints

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| GET | `/api/members` | List all members ordered by name | – | `200 OK` array of `Member` |
| GET | `/api/members/{id}` | Retrieve a member by id | – | `200 OK` `Member` or `404` |
| POST | `/api/members` | Register a new member | JSON `Member` | `200 OK` created `Member`, `400` validation, `409` email taken |

### Example `POST /api/members`
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "phoneNumber": "9876543210"
}
```

## 6 – Prerequisites
1. **Java 21** installed (`java -version`).
2. **Maven 3.9+** (`mvn -version`).
3. Optional: IDE with Lombok plugin (IntelliJ / VS Code).

## 7 – Build & Run

```bash
# compile & unit tests
mvn clean verify

# run the application (dev profile, hot reload)
mvn spring-boot:run

# or build an executable JAR
mvn clean package
java -jar target/kitchensink-spring-boot-1.0.0-SNAPSHOT.jar
```

The service starts on **http://localhost:8080**.  
Access the H2 console at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:kitchensinkdb`, user `sa`, blank password).

## 8 – Testing

* **Integration tests** live in `src/test/java/**`.  
  * 12 scenarios cover repository, service, and REST endpoints.
* Run all tests via:
  ```bash
  mvn test
  ```
* Tests use an isolated H2 database (`application-test.properties`).

## 9 – Further Improvements
* Switch in-memory H2 to PostgreSQL/MySQL for production.
* Add DTOs & MapStruct to decouple API from JPA entities.
* Containerise with Docker and deploy via Kubernetes Helm chart.

---

© 2025 Factory. Licensed under the Apache 2.0 License.
