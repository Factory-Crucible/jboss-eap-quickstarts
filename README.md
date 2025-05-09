# Kitchensink Spring Boot Application

## Overview

This project is a Spring Boot migration of the JBoss EAP "kitchensink" quickstart application. It demonstrates how to build a simple, yet fully functional Java web application using Spring Boot 3.x. The application allows users to register as members and view the list of all registered members through a RESTful API.

## Prerequisites

- Java 17 or higher
- Maven 3.8+ or Gradle 7.5+
- Git (optional, for version control)
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Project Structure

```
src/main/java/org/jboss/as/quickstarts/kitchensink/
├── config/                  # Configuration classes
├── controller/              # REST controllers
├── exception/               # Exception handling
├── model/                   # Domain model classes
├── repository/              # Spring Data JPA repositories
├── service/                 # Business logic services
├── util/                    # Utility classes
└── KitchensinkApplication.java  # Main application class
```

## Building the Application

### Using Maven

```bash
# Clone the repository (if using Git)
git clone <repository-url>
cd kitchensink-spring

# Build the application
./mvnw clean package
```

### Using Gradle

```bash
# Build the application
./gradlew clean build
```

## Running the Application

### Using Maven

```bash
./mvnw spring-boot:run
```

### Using Gradle

```bash
./gradlew bootRun
```

### Using the JAR file

```bash
java -jar target/kitchensink-spring-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080 with context path `/kitchensink`. You can access it at:
http://localhost:8080/kitchensink

## Testing the Application

### Running the Tests

```bash
# Using Maven
./mvnw test

# Using Gradle
./gradlew test
```

### Manual Testing with curl

#### Get all members:

```bash
curl -X GET http://localhost:8080/kitchensink/api/members
```

#### Get a specific member:

```bash
curl -X GET http://localhost:8080/kitchensink/api/members/1
```

#### Create a new member:

```bash
curl -X POST http://localhost:8080/kitchensink/api/members \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com","phoneNumber":"1234567890"}'
```

## REST API Endpoints

| Method | URL                                  | Description                | Request Body | Success Response |
|--------|--------------------------------------|----------------------------|--------------|------------------|
| GET    | `/api/members`                       | Get all members            | None         | 200 OK           |
| GET    | `/api/members/{id}`                  | Get member by ID           | None         | 200 OK           |
| POST   | `/api/members`                       | Create a new member        | Member JSON  | 201 Created      |

### Example Member JSON

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

## Application Architecture

The application follows a standard layered architecture:

1. **Presentation Layer** (`controller` package):
   - `MemberController`: Handles HTTP requests and responses for the RESTful API

2. **Service Layer** (`service` package):
   - `MemberService`: Implements business logic for member registration and retrieval
   - `MemberValidationService`: Validates member data, including email uniqueness

3. **Repository Layer** (`repository` package):
   - `MemberRepository`: Spring Data JPA repository for database operations

4. **Domain Model** (`model` package):
   - `Member`: JPA entity representing a member with validation annotations

5. **Exception Handling** (`exception` package):
   - `GlobalExceptionHandler`: Centralized exception handling for consistent error responses

6. **Configuration** (`config` package):
   - `LoggerConfig`: Configuration for logging

### Key Technologies

- **Spring Boot**: Provides the foundation for the application
- **Spring MVC**: Handles HTTP requests and responses
- **Spring Data JPA**: Simplifies database access and ORM
- **H2 Database**: In-memory database for development and testing
- **Jakarta Bean Validation**: Validates domain model objects
- **JUnit 5**: Testing framework for unit and integration tests

## Development Features

- **H2 Console**: Available at http://localhost:8080/kitchensink/h2-console
  - JDBC URL: `jdbc:h2:mem:kitchensink`
  - Username: `sa`
  - Password: (empty)

- **Spring Boot DevTools**: Enables automatic restart during development

## Differences from Original JBoss Application

- Replaced EJB (`@Stateless`) with Spring `@Service` components
- Replaced JAX-RS with Spring MVC for REST endpoints
- Replaced CDI with Spring's dependency injection
- Replaced manual JPA queries with Spring Data JPA repositories
- Added Spring Boot's auto-configuration for datasource and JPA
- Centralized exception handling with `@RestControllerAdvice`
- Constructor-based dependency injection instead of field injection
- Return types that leverage Java 8+ features like `Optional<T>`

## Configuration Properties

The application's configuration can be customized through `application.properties`. Key properties include:

- `spring.datasource.*`: Database connection settings
- `spring.jpa.*`: JPA and Hibernate configuration
- `server.port`: HTTP port (default: 8080)
- `server.servlet.context-path`: Application context path (default: /kitchensink)

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
