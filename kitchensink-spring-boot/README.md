# Kitchensink Spring Boot Migration

Welcome to **kitchensink-spring-boot** – a modernized edition of the famous JBoss EAP “kitchensink” quick-start.  
The goal of this project is **feature-parity migration** of the original Jakarta EE application to **Spring Boot 3.2 / Java 21** while embracing Factory engineering standards.

---

## 1. Technology Mapping

| Original Jakarta EE (JBoss EAP) | Spring Boot Replacement | Notes |
|---------------------------------|-------------------------|-------|
| CDI `@Inject`, `@ApplicationScoped`, `@RequestScoped` | Spring DI / `@Component`, `@Service`, `@RestController` | Annotation-driven injection instead of container scopes |
| EJB `@Stateless` (transactional) | Spring `@Service` + `@Transactional` | Declarative transactions via Spring TX |
| JPA with `EntityManager` | **Spring Data JPA** (`JpaRepository`) | Eliminates boilerplate queries |
| JAX-RS (`@Path`, `@GET`, `@POST`) | Spring MVC (`@RequestMapping`, `@GetMapping`, `@PostMapping`) | Same JSON payloads |
| Bean Validation | Same (Jakarta Validation) | Handled automatically by Spring |
| `persistence.xml`, datasource XML | `application.properties` | YAML/Properties driven config |
| Arquillian integration tests | Spring Boot Test + TestRestTemplate | Embedded server, no container needed |

---

## 2. Building & Running

```bash
# Prerequisites
# - JDK 21+
# - Maven 3.9+

# Clone repo & move to module
git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
cd jboss-eap-quickstarts/kitchensink-spring-boot

# Build (compiles+tests)
mvn clean verify

# Run the application
mvn spring-boot:run
# or
java -jar target/kitchensink-spring-boot-1.0.0.jar
```

Application starts on **http://localhost:8080/kitchensink**.

---

## 3. REST API

Base path: `/kitchensink/api/members`

| Method | Endpoint | Description | Response codes |
|--------|----------|-------------|----------------|
| GET    | `/` | List all members ordered by name | 200 |
| GET    | `/{id}` | Get member by ID | 200, 404 |
| POST   | `/` | Create a member (`name`, `email`, `phoneNumber`) | 201, 400 (validation), 409 (duplicate email) |

All responses are JSON.

### Example cURL

```bash
curl -X POST http://localhost:8080/kitchensink/api/members \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane.doe@example.com","phoneNumber":"2025551234"}'
```

---

## 4. Database (H2)

During development the app uses an **in-memory H2** database seeded via `data.sql`.

• Console: `http://localhost:8080/kitchensink/h2-console`  
• JDBC URL: `jdbc:h2:mem:kitchensink`  
• User: `sa`, Password: *(empty)*

---

## 5. Migration Process & Key Changes

1. **Project Setup** – Spring Initializr, Maven parent `spring-boot-starter-parent`.
2. **Domain Model** – `Member` annotated with Lombok `@Data`, keeps all validation constraints.
3. **Repository** – replaced manual Criteria API with `MemberRepository extends JpaRepository`.
4. **Business Logic** – EJB ➜ `MemberService` (`@Service`, `@Transactional`).
5. **REST Layer** – JAX-RS ➜ `MemberController` using Spring MVC.
6. **Exception Handling** – centralized `GlobalExceptionHandler` with uniform JSON error bodies.
7. **Config** – XML descriptors removed; replaced by `application.properties`.
8. **Tests** – Arquillian tests migrated to:
   * `KitchensinkApplicationTests` (12 integration scenarios)
   * Mockito-based `MemberServiceTest`
9. **Executable Artifact** – fat JAR (self-contained) instead of EAR/WAR.

---

## 6. Running Tests

```bash
mvn test            # unit tests
mvn verify          # integration + unit tests
```

Integration tests start the app on a random port and hit real HTTP endpoints using TestRestTemplate.

---

## 7. Dependencies & Requirements

* Java 21
* Spring Boot 3.2.1
* Spring Data JPA
* H2 Database (runtime/dev)
* Lombok (compile-time)
* Spring Boot Test, JUnit 5, Mockito

See `pom.xml` for full list.

---

## 8. Troubleshooting

| Symptom | Possible Cause | Resolution |
|---------|----------------|------------|
| `Port 8080 already in use` | Another process running | Stop process or change `server.port` |
| `Cannot connect to H2 console` | Console disabled in prod profile | Ensure `spring.h2.console.enabled=true` |
| 409 Conflict on create | Email already exists | Use unique email or purge DB |
| Validation errors 400 | Field constraints violated | Check response JSON for `fieldErrors` map |
| Build fails on Lombok | IDE missing Lombok plugin | Install Lombok plugin / enable annotation processing |

---

## 9. Improvements Over Original

* **Single-Jar Deployment** – easier containerization & CI/CD.
* **Spring Data JPA** – removes verbose Criteria queries.
* **Centralized Error Handling** – consistent JSON errors.
* **Modern Java 21 Features** – records, pattern matching available for future work.
* **DevTools & LiveReload** – faster local development.
* **Observable Endpoints** – Spring Actuator endpoints `/actuator/health`, `/actuator/info`.
* **Extensive Test Coverage** – 12 integration + thorough unit tests (none in original release build).
* **Property-based Configuration** – environment-specific configs without XML.

---

Happy coding! 🎉
