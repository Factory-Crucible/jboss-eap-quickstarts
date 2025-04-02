# Kitchensink Spring Boot Application

## Introduction

This application is a Spring Boot migration of the JBoss EAP "kitchensink" quickstart. It demonstrates a Jakarta EE web-enabled database application using Spring Boot, Spring Data JPA, Spring Web, and Bean Validation. The application provides a member registration system with REST API endpoints for managing members.

## Project Structure

The project follows a standard Spring Boot application structure:

```
kitchensink-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── kitchensink/
│   │   │               ├── controller/    # REST controllers
│   │   │               ├── exception/     # Custom exceptions and handlers
│   │   │               ├── model/         # Entity classes
│   │   │               ├── repository/    # Spring Data JPA repositories
│   │   │               ├── service/       # Business logic services
│   │   │               └── KitchensinkApplication.java  # Main class
│   │   └── resources/
│   │       ├── application.properties     # Application configuration
│   │       └── import.sql                 # Sample data initialization
│   └── test/
│       └── java/                          # Test classes
└── build.gradle                           # Gradle build configuration
```

## Technologies Used

- **Spring Boot 3.1.0** - Main framework
- **Spring Data JPA** - Data access layer
- **Spring Web** - REST API
- **H2 Database** - In-memory database
- **Lombok** - Reduces boilerplate code
- **Jakarta Validation** - Bean validation
- **Gradle** - Build tool
- **Java 21** - Programming language

## Building and Running the Application

### Prerequisites

- Java 21 JDK
- Gradle 7.x or later (or use the included Gradle wrapper)

### Building the Application

```bash
# Clone the repository
git clone <repository-url>
cd kitchensink-spring-boot

# Build the application
./gradlew build
```

### Running the Application

```bash
# Run the application
./gradlew bootRun
```

The application will be available at: http://localhost:8080/kitchensink

### Running Tests

```bash
./gradlew test
```

## Key Differences from Original JBoss Version

1. **Framework**: Migrated from JBoss EAP/Jakarta EE to Spring Boot

2. **Build System**: Changed from Maven to Gradle

3. **Architecture Changes**:
   - Replaced EJB with Spring Services
   - Replaced JAX-RS with Spring Web REST controllers
   - Replaced JPA repositories with Spring Data JPA repositories
   - Replaced CDI with Spring dependency injection

4. **Simplified Code**:
   - Used Lombok to reduce boilerplate code
   - Utilized Spring Data JPA for repository implementations
   - Implemented centralized exception handling

5. **Configuration**:
   - Replaced XML configurations with application.properties
   - Simplified datasource configuration

## REST API Endpoints

The application exposes the following REST endpoints:

- `GET /kitchensink/rest/members` - Get all members
- `GET /kitchensink/rest/members/{id}` - Get a member by ID
- `POST /kitchensink/rest/members` - Register a new member

### Example POST Request

```bash
curl -X POST http://localhost:8080/kitchensink/rest/members \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com","phoneNumber":"2125551234"}'
```

## Database

The application uses an H2 in-memory database by default. The database is initialized with sample data from the `import.sql` file.

### H2 Console

The H2 console is enabled and available at: http://localhost:8080/kitchensink/h2-console

Connection details:
- JDBC URL: `jdbc:h2:mem:kitchensink`
- Username: `sa`
- Password: `password`

## Validation Rules

The Member entity includes the following validation rules:

- **name**: Required, between 1-25 characters, must not contain numbers
- **email**: Required, must be a valid email address
- **phoneNumber**: Required, between 10-12 digits

## Future Improvements

1. Add a web UI using Thymeleaf or a modern frontend framework
2. Implement security with Spring Security
3. Add support for other databases like PostgreSQL or MongoDB
4. Implement pagination for member listing
5. Add more comprehensive unit and integration tests
