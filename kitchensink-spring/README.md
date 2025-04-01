# Kitchensink Spring Boot Migration

This project is a migration of the JBoss EAP Kitchensink quickstart application to Spring Boot. It demonstrates how to convert a Jakarta EE application to a modern Spring Boot application while preserving the core functionality.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Key Differences](#key-differences)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Deployment](#deployment)

## Overview

The original JBoss Kitchensink application demonstrates Jakarta EE technologies including CDI, JSF, JPA, EJB, JAX-RS, and Bean Validation. This Spring Boot version maintains the core functionality while replacing the Jakarta EE technologies with their Spring equivalents.

### Features

- Member registration and management
- Validation of member data
- Persistence using JPA
- RESTful API endpoints
- Event-driven architecture

## Architecture

The application follows a standard Spring Boot layered architecture:

```
kitchensink-spring/
├── src/main/java/
│   └── org/jboss/eap/quickstarts/kitchensink/
│       ├── KitchensinkApplication.java       # Main Spring Boot application class
│       ├── model/                            # Domain model
│       │   └── Member.java                   # Member entity
│       ├── repository/                       # Data access layer
│       │   └── MemberRepository.java         # Spring Data JPA repository
│       ├── service/                          # Business logic layer
│       │   ├── MemberService.java            # Service interface
│       │   └── MemberServiceImpl.java        # Service implementation
│       ├── rest/                             # REST API layer
│       │   └── MemberController.java         # Spring REST controller
│       ├── event/                            # Event handling
│       │   └── MemberEventListener.java      # Spring event listener
│       └── exception/                        # Exception handling
│           └── GlobalExceptionHandler.java   # Global exception handler
└── src/main/resources/
    ├── application.properties                # Application configuration
    └── import.sql                            # Database initialization
```

## Key Differences

| Jakarta EE (JBoss) | Spring Boot | Notes |
|-------------------|-------------|-------|
| CDI (`@Inject`) | Spring DI (`@Autowired`) | Spring's dependency injection replaces CDI |
| EJB (`@Stateless`) | Spring (`@Service`) | Spring services replace EJBs |
| JAX-RS (`@Path`, `@GET`, etc.) | Spring MVC (`@RestController`, `@GetMapping`, etc.) | Spring MVC annotations replace JAX-RS |
| CDI Events | Spring Events | Spring's ApplicationEventPublisher replaces CDI events |
| JSF UI | None (API only) | This migration focuses on the backend only |
| Bean Validation | Bean Validation | Same validation annotations are used |
| JPA | Spring Data JPA | JPA entities remain similar, but repositories are simplified |

## Prerequisites

- Java 21 or later
- Maven 3.6.0 or later
- Git

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts/kitchensink-spring
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

## Running the Application

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. The application will be available at:
   ```
   http://localhost:8080/kitchensink
   ```

3. Access the H2 console at:
   ```
   http://localhost:8080/kitchensink/h2-console
   ```
   - JDBC URL: `jdbc:h2:mem:kitchensink`
   - Username: `sa`
   - Password: `sa`

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members` | Get all members |
| GET | `/api/members/{id}` | Get member by ID |
| GET | `/api/members/email/{email}` | Get member by email |
| POST | `/api/members` | Create a new member |
| PUT | `/api/members/{id}` | Update an existing member |
| DELETE | `/api/members/{id}` | Delete a member |
| GET | `/api/members/exists/{email}` | Check if email exists |

### Example Request

```bash
# Create a new member
curl -X POST http://localhost:8080/kitchensink/api/members \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com","phoneNumber":"1234567890"}'
```

## Testing

Run the tests using:

```bash
mvn test
```

## Deployment

### Building a JAR

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

### Running with Java

```bash
java -jar target/kitchensink-spring-1.0.0.jar
```

### Docker Deployment

1. Build a Docker image:
   ```bash
   docker build -t kitchensink-spring .
   ```

2. Run the Docker container:
   ```bash
   docker run -p 8080:8080 kitchensink-spring
   ```

## Migration Notes

### Database Configuration

The original JBoss application used a datasource defined in `kitchensink-quickstart-ds.xml`. In Spring Boot, this is replaced with configuration in `application.properties`.

### Transaction Management

Spring's `@Transactional` annotation replaces the EJB transaction management. The behavior is similar, but Spring offers more fine-grained control over transaction attributes.

### REST Endpoints

The REST endpoints functionality is preserved, but the implementation uses Spring MVC annotations instead of JAX-RS. The URL structure remains similar for compatibility.

### Validation

Bean Validation annotations are maintained, but the validation handling is now done through Spring's validation framework and the global exception handler.

### Event System

CDI events are replaced with Spring's event system, which provides similar functionality but with a different API.
