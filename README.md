# Kitchensink: JBoss to Spring Boot 3 Migration

This project is a migration of the JBoss EAP 'kitchensink' quickstart application to a modern Spring Boot 3 application. It demonstrates how to build a complete web application using Spring Boot, Spring Data JPA, and RESTful web services.

## Project Overview

The original JBoss 'kitchensink' application was built using Jakarta EE technologies. This migration preserves all the functionality while leveraging Spring Boot's convention-over-configuration approach, dependency injection, and embedded server capabilities.

### Key Features

- RESTful API for member management (CRUD operations)
- JPA-based persistence with Hibernate
- Bean validation
- Exception handling
- Event-driven architecture using Spring's event system
- Comprehensive testing

## Project Structure

```
src/main/java/com/example/kitchensink/
├── KitchensinkApplication.java       # Main application class
├── controller/                       # REST controllers
│   └── MemberController.java         # Endpoints for member operations
├── event/                            # Event classes
│   ├── MemberEventListener.java      # Event listener for member events
│   └── MemberRegisteredEvent.java    # Event published when a member is registered
├── exception/                        # Exception handling
│   ├── DuplicateResourceException.java
│   ├── GlobalExceptionHandler.java   # Global exception handler
│   └── ResourceNotFoundException.java
├── model/                            # Domain model
│   └── Member.java                   # Member entity
├── repository/                       # Data access layer
│   └── MemberRepository.java         # Spring Data JPA repository
└── service/                          # Business logic
    ├── MemberService.java            # Service interface
    └── impl/
        └── MemberServiceImpl.java    # Service implementation
```

## Components

### Domain Model

The `Member` entity represents a person with the following attributes:
- ID (auto-generated)
- Name (with validation)
- Email (unique, with validation)
- Phone number (with validation)

### Repository Layer

The `MemberRepository` extends Spring Data JPA's `JpaRepository` to provide CRUD operations and custom query methods for the `Member` entity.

### Service Layer

The `MemberService` contains the business logic for member operations:
- Finding members (by ID, email, or all)
- Registering new members
- Updating existing members
- Deleting members

The service also publishes events when certain operations are performed, such as when a member is registered.

### Controller Layer

The `MemberController` exposes RESTful endpoints for member operations:
- `GET /api/members`: Get all members
- `GET /api/members/{id}`: Get a member by ID
- `POST /api/members`: Create a new member
- `PUT /api/members/{id}`: Update an existing member
- `DELETE /api/members/{id}`: Delete a member

### Exception Handling

The `GlobalExceptionHandler` provides centralized exception handling for the application, returning appropriate HTTP status codes and error messages for different types of exceptions.

## Prerequisites

- Java 17 or later
- Maven 3.6.3 or later
- An IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Running the Application

### Using Maven

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/kitchensink.git
   cd kitchensink
   ```

2. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. The application will be available at:
   ```
   http://localhost:8080/kitchensink
   ```

### Using Docker

1. Build the Docker image:
   ```bash
   docker build -t kitchensink .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 kitchensink
   ```

## API Documentation

Once the application is running, you can access the OpenAPI documentation at:
```
http://localhost:8080/kitchensink/swagger-ui.html
```

## Database

The application uses an H2 in-memory database by default for development. You can access the H2 console at:
```
http://localhost:8080/kitchensink/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:kitchensinkdb`
- Username: `sa`
- Password: `password`

For production, the application is configured to use MySQL. You can configure the database connection in the `application.yml` file or using environment variables.

## Testing

The application includes comprehensive tests:

### Unit Tests

Unit tests focus on testing individual components in isolation, using mocks for dependencies. For example, the `MemberServiceTest` tests the service layer with mocked repository and event publisher.

### Integration Tests

Integration tests verify that different components work together correctly. These tests use the Spring Boot test framework to create a test application context.

### Running Tests

```bash
mvn test
```

## Configuration

The application uses YAML configuration files with profiles for different environments:
- `dev`: Development environment with H2 database and detailed logging
- `test`: Testing environment with in-memory database
- `prod`: Production environment with MySQL database and optimized settings

You can specify the active profile using the `spring.profiles.active` property:
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Migration Notes

### Key Differences from JBoss Implementation

1. **Dependency Injection**: Spring Boot uses constructor injection instead of field injection with `@Inject`
2. **Repository Layer**: Spring Data JPA repositories replace JBoss's EntityManager-based repositories
3. **Event Handling**: Spring's `ApplicationEventPublisher` replaces CDI events
4. **REST Controllers**: Spring's `@RestController` and HTTP method annotations replace JAX-RS annotations
5. **Configuration**: YAML-based configuration replaces XML configuration files
6. **Embedded Server**: Spring Boot includes an embedded Tomcat server, eliminating the need for a separate application server

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
