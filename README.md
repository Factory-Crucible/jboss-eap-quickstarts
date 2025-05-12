# Kitchensink Spring Boot Application

## Overview

This application is a migration of the JBoss EAP 'kitchensink' quickstart to Spring Boot. It demonstrates how to build a simple yet fully functional Spring Boot application that manages a member registry with basic CRUD operations through a RESTful API.

The application allows users to:
- Register new members with name, email, and phone number
- View a list of all registered members
- Look up members by ID
- Validate member data using Bean Validation

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.6**
- **Spring Data JPA** - For data access and persistence
- **Spring MVC** - For REST API endpoints
- **H2 Database** - In-memory database for development
- **JUnit 5** - For testing
- **Spring Boot Test** - For integration testing

## Prerequisites

- JDK 17 or later
- Maven 3.6.3 or later
- Git (optional)

## Building the Application

1. Clone the repository (if you haven't already):
   ```bash
   git clone <repository-url>
   cd kitchensink-spring
   ```

2. Build the application using Maven:
   ```bash
   mvn clean package
   ```

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java

```bash
java -jar target/kitchensink-spring-1.0.0.jar
```

The application will start on port 8080 with context path `/kitchensink`. The H2 console is available at `/kitchensink/h2-console` (JDBC URL: `jdbc:h2:mem:kitchensink`, username: `sa`, password: empty).

## API Endpoints

| Method | URL                       | Description                     |
|--------|---------------------------|---------------------------------|
| GET    | `/kitchensink/api/members`      | List all members               |
| GET    | `/kitchensink/api/members/{id}` | Get a member by ID             |
| POST   | `/kitchensink/api/members`      | Create a new member            |

### Example Request for Creating a Member

```bash
curl -X POST http://localhost:8080/kitchensink/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "1234567890"
  }'
```

## Migration from JBoss EAP to Spring Boot

This application was migrated from the JBoss EAP 'kitchensink' quickstart, which was built using Jakarta EE technologies. The migration involved converting:

1. **Jakarta EE to Spring Boot**:
   - Jakarta CDI → Spring Dependency Injection
   - Jakarta EJB → Spring Services with @Transactional
   - JAX-RS → Spring MVC REST Controllers
   - JPA EntityManager → Spring Data JPA Repositories

2. **Key Migration Points**:
   - Replaced direct EntityManager usage with Spring Data JPA repositories
   - Converted JAX-RS endpoints to Spring MVC @RestController
   - Replaced EJB @Stateless with Spring @Service and @Transactional
   - Replaced CDI events with Spring ApplicationEventPublisher
   - Maintained the same validation constraints and business logic

3. **Configuration Changes**:
   - Replaced XML configuration with Spring Boot properties and annotations
   - Added Spring Boot auto-configuration
   - Simplified dependency management with Spring Boot starters

## Key Components

### Domain Model

The `Member` entity represents a registered user with the following attributes:
- `id` - Unique identifier
- `name` - User's name (must not contain numbers)
- `email` - User's email address (must be valid and unique)
- `phoneNumber` - User's phone number (must be 10-12 digits)

### Data Access Layer

The `MemberRepository` interface extends Spring Data JPA's `JpaRepository` to provide:
- Standard CRUD operations
- Custom finder methods like `findByEmail` and `findAllByOrderByNameAsc`

### Service Layer

The `MemberRegistration` service handles:
- Member registration business logic
- Transaction management
- Event publishing when a member is registered

### REST API

The `MemberController` provides RESTful endpoints for:
- Listing all members
- Looking up a member by ID
- Creating new members with validation

### Exception Handling

The `GlobalExceptionHandler` provides centralized exception handling for:
- Validation errors
- Entity not found errors
- General application exceptions

## Testing

The application includes comprehensive tests:
- Unit tests for the domain model
- Repository tests for data access
- Controller tests for the REST API

Run the tests using:
```bash
mvn test
```

## Development

### H2 Console

The H2 in-memory database console is enabled for development and can be accessed at:
```
http://localhost:8080/kitchensink/h2-console
```

Use the following settings:
- JDBC URL: `jdbc:h2:mem:kitchensink`
- Username: `sa`
- Password: leave empty

### Spring Boot DevTools

The application includes Spring Boot DevTools for enhanced development experience:
- Automatic restarts when code changes
- LiveReload support
- Enhanced development-time error reporting

## License

This project is licensed under the Apache License 2.0 - see the original JBoss EAP quickstarts repository for details.
