# Kitchensink Spring Boot Migration

This project is a migration of the JBoss EAP Kitchensink quickstart application to Spring Boot 3.2 with Java 21. The application demonstrates a simple member registration system with CRUD operations.

## Table of Contents

- [Overview](#overview)
- [Technologies](#technologies)
- [Migration Details](#migration-details)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Key Differences](#key-differences)
- [Future Improvements](#future-improvements)

## Overview

The Kitchensink application is a simple web application that allows users to register as members, view a list of members, and manage member information. This Spring Boot version maintains all the core functionality of the original JBoss EAP application while modernizing the technology stack.

## Technologies

- **Java 21**
- **Spring Boot 3.2.3**
- **Spring Data JPA** - For data access
- **H2 Database** - In-memory database for development
- **Jakarta Bean Validation** - For input validation
- **Spring Web** - For REST API
- **Lombok** - To reduce boilerplate code
- **Spring Boot Actuator** - For application monitoring
- **Maven** - Build and dependency management

## Migration Details

The migration from JBoss EAP to Spring Boot involved several key transformations:

| JBoss Component | Spring Boot Equivalent |
|----------------|------------------------|
| `Member.java` (JPA Entity) | Spring Data JPA Entity |
| `MemberRepository.java` | Spring Data JPA Repository |
| `MemberListProducer.java` | Service Layer |
| `MemberRegistration.java` (EJB) | Spring Service with `@Service` |
| `MemberResourceRESTService.java` | Spring REST Controller |
| `JaxRsActivator.java` | Spring Boot Auto-configuration |
| `persistence.xml` | `application.properties` |
| Bean Validation | Jakarta Bean Validation |
| CDI Events | Spring ApplicationEvents |

Key migration steps included:
1. Updating package names and structure
2. Converting JAX-RS annotations to Spring Web annotations
3. Replacing EJB with Spring Services
4. Converting CDI to Spring dependency injection
5. Replacing `persistence.xml` with Spring Boot configuration
6. Updating `javax.*` imports to `jakarta.*`
7. Implementing proper exception handling

## Project Structure

```
kitchensink-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── kitchensink/
│   │   │               ├── controller/
│   │   │               ├── event/
│   │   │               ├── exception/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── KitchensinkApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── data.sql
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── kitchensink/
│                       ├── controller/
│                       ├── repository/
│                       └── service/
└── pom.xml
```

## Getting Started

### Prerequisites

- Java 21 or later
- Maven 3.6.0 or later

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/kitchensink-spring-boot.git
   cd kitchensink-spring-boot
   ```

2. Build the application:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   
   Alternatively, you can run the JAR file directly:
   ```bash
   java -jar target/kitchensink-1.0.0.jar
   ```

4. Access the application at http://localhost:8080/kitchensink

### H2 Console

The H2 console is enabled for development purposes and can be accessed at:
http://localhost:8080/kitchensink/h2-console

Connection details:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/members` | Get all members |
| GET    | `/api/members/{id}` | Get member by ID |
| POST   | `/api/members` | Create a new member |
| PUT    | `/api/members/{id}` | Update an existing member |
| DELETE | `/api/members/{id}` | Delete a member |
| GET    | `/api/members/email/{email}` | Check if email exists |

### Example Requests

#### Create a new member

```bash
curl -X POST http://localhost:8080/kitchensink/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane.doe@example.com",
    "phoneNumber": "1234567890"
  }'
```

#### Get all members

```bash
curl -X GET http://localhost:8080/kitchensink/api/members
```

## Key Differences

Compared to the original JBoss EAP application, this Spring Boot version has the following differences:

1. **Technology Stack**: Uses Spring Boot instead of Jakarta EE (formerly Java EE)
2. **Dependency Injection**: Uses Spring DI instead of CDI
3. **REST Implementation**: Uses Spring Web instead of JAX-RS
4. **Transaction Management**: Uses Spring's declarative transaction management instead of EJB
5. **Configuration**: Uses `application.properties` instead of XML files
6. **Packaging**: Produces an executable JAR instead of a WAR file
7. **UI Layer**: The original JSF UI has been removed in favor of a pure REST API

## Future Improvements

1. Add a modern frontend using React, Angular, or Vue.js
2. Implement Spring Security for authentication and authorization
3. Add Swagger/OpenAPI documentation
4. Implement pagination for the member list endpoint
5. Add more comprehensive unit and integration tests
6. Set up CI/CD pipeline
7. Add Docker support for containerization
8. Implement database migration tools like Flyway or Liquibase
9. Add support for multiple environments (dev, test, prod)
10. Implement caching for improved performance
