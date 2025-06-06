# Spring Boot Kitchensink

Migrated sample application demonstrating typical Spring Boot patterns while preserving the original JBoss **kitchensink** business logic and package names (`org.jboss.as.quickstarts.kitchensink`).  
It exposes a small **Member Registration** REST API backed by an in-memory H2 database.

## Features

* Create and list members (`name`, `email`, `phoneNumber`)
* Bean-validation with detailed error reporting
* Spring Data JPA repository with automatically generated queries
* Transactional service layer that publishes an application event on successful registration
* Global exception handling (`@ControllerAdvice`)
* Seed data loaded from `import.sql`
* H2 web console enabled at `/h2-console` (dev only)

## Building & Running

### Prerequisites
* JDK 21+
* Maven 3.9+

### Build

```bash
mvn clean package
```

Creates `spring-kitchensink-0.0.1-SNAPSHOT.jar` in the `target` folder.

### Run

```bash
# Option 1 – Maven
mvn spring-boot:run

# Option 2 – Executable JAR
java -jar target/spring-kitchensink-0.0.1-SNAPSHOT.jar
```

The application starts on `http://localhost:8080/kitchensink`.

### Quick API check

```bash
curl -X GET http://localhost:8080/kitchensink/rest/members
```

### H2 Console

Open `http://localhost:8080/kitchensink/h2-console`  
• JDBC URL: `jdbc:h2:mem:kitchensink`  
• User: `sa` (empty password)

## REST Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET    | `/rest/members` | List all members |
| GET    | `/rest/members/{id}` | Get member by id |
| POST   | `/rest/members` | Register a new member (JSON body) |

### Sample `POST` Body

```json
{
  "name": "Alice Example",
  "email": "alice@example.com",
  "phoneNumber": "12345678901"
}
```

## Migration Highlights (JBoss EAP → Spring Boot)

| Concern | JBoss EAP / Jakarta EE | Spring Boot Equivalent |
|---------|------------------------|------------------------|
| Dependency Injection | CDI `@Inject` | Spring DI / constructor injection |
| EJB Business Bean | `@Stateless` `MemberRegistration` | `@Service` + `@Transactional` |
| REST | JAX-RS `@Path` resources & `JaxRsActivator` | `@RestController` + `@RequestMapping` |
| Persistence Unit | `persistence.xml` | `application.properties` (`spring.jpa.*`) |
| Entity Manager | `EntityManager` injected | Spring Data JPA `MemberRepository` |
| Producer Utilities | `Resources.java` CDI producer | `@Bean` producer in `ApplicationConfig` |
| Eventing | CDI `Event<Member>` | `ApplicationEventPublisher` |
| Testing | Arquillian ITs | Spring Boot Test (`@WebMvcTest`, Mockito) |

Design decisions:
* **Package retention** – package names kept intact for minimal refactor effort.
* **H2** chosen for dev parity with original example.
* **Spring Boot 3.2.5** selected (no org-specific version constraints).

## Further Work

Phase 3 of the migration (database swap, Docker/K8s, etc.) can now build on this Spring foundation.

---
© Red Hat & contributors – migrated to Spring Boot by Factory Engineering (Linear PRO-339).
