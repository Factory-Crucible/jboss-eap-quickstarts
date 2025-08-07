# Kitchensink Spring Boot

A modern Spring Boot 3 migration of the classic JBoss EAP **kitchensink** quick-start.  
The goal is **feature parity** with the original Jakarta EE application while adopting Factory engineering standards (Spring Boot 3, Java 21, layered architecture, tests).

## Technologies & Libraries

| Purpose               | Dependency / Version            |
|-----------------------|---------------------------------|
| Runtime Framework     | Spring Boot **3.2.1**           |
| Language              | **Java 21** (preview features disabled) |
| Persistence           | Spring Data JPA + Hibernate     |
| Database (dev/test)   | **H2 in-memory**                |
| Validation            | Jakarta Bean Validation (via `spring-boot-starter-validation`) |
| Build Tool            | **Maven**                       |
| Boilerplate Reduction | **Lombok**                      |
| Tests                 | Spring Boot Test, JUnit 5, TestRestTemplate |

## Prerequisites

* JDK 21 installed and `JAVA_HOME` set  
* Maven 3.9+ (`mvn -v`)  
* (Optional) cURL or HTTPie for API testing

## Getting Started

### 1. Build

```bash
mvn clean package
```

The command produces `target/kitchensink-spring-boot-0.0.1-SNAPSHOT.jar`.

### 2. Run

```bash
# Option A – via Spring Boot Maven plugin
mvn spring-boot:run

# Option B – run the packaged JAR
java -jar target/kitchensink-spring-boot-0.0.1-SNAPSHOT.jar
```

The application starts on **http://localhost:8080**.

### 3. H2 Console

Access the in-memory DB UI at `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:kitchensinkdb` – user `sa`, password `password`.

## REST API Reference

Base path: **/api/members**

| Method | Path            | Purpose                     | Status Codes |
|--------|-----------------|-----------------------------|--------------|
| GET    | `/api/members`  | List all members            | 200 OK       |
| GET    | `/api/members/{id}` | Get member by ID       | 200 OK, 404 Not Found |
| POST   | `/api/members`  | Register a new member       | 201 Created, 400 Bad Request, 409 Conflict |

### Member JSON schema (simplified)

```json
{
  "id": 1,
  "name": "John Smith",
  "email": "john.smith@mailinator.com",
  "phoneNumber": "2125551212"
}
```

### Example Calls

List members:

```bash
curl -s http://localhost:8080/api/members | jq
```

Get member by ID:

```bash
curl -s http://localhost:8080/api/members/1
```

Create member:

```bash
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Ada Lovelace",
        "email": "ada@example.com",
        "phoneNumber": "5551234567"
      }'
```

Error example — duplicate e-mail:

```json
HTTP/1.1 409 Conflict
{
  "email": "Email taken"
}
```

## Testing

Run the full test suite:

```bash
mvn test
```

* `KitchensinkApplicationTests` – context load smoke test  
* `MemberServiceTest` – service-layer integration  
* `MemberControllerIntegrationTest` – end-to-end REST tests using `TestRestTemplate` on a random port

Coverage includes 12 scenarios (happy paths + validation and conflict cases).

## Development Notes

* **Hot reload:** `spring-boot-devtools` enables automatic restart on code changes.
* **Logging:** set in `application.properties`. Default app package logs at DEBUG.
* **Database schema:** generated at startup (`spring.jpa.hibernate.ddl-auto=create-drop`).
* **Sample data:** inserted via `data.sql`.
* **Port change:** edit `server.port` in `application.properties`.
* **Future work:** MongoDB phase (optional), containerization, CI workflow.

---

© Factory AI – Migration ticket **PRO-339**  
Branch: `droid-pro-339`
