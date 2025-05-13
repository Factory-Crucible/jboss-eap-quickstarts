# Kitchensink Spring Boot Application

This project is a migration of the JBoss EAP Kitchensink application to Spring Boot. It demonstrates a simple member registration application using Spring Boot, Spring Data JPA, and Spring MVC for REST APIs.

## Overview

The application provides a REST API for managing members, including:
- Registering new members
- Listing all members
- Finding members by ID or email

The project follows modern Spring Boot best practices with a layered architecture:
- Controller Layer: Handles HTTP requests and responses
- Service Layer: Contains business logic
- Repository Layer: Interfaces with the database
- Model Layer: Contains domain entities and DTOs

## Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- Git (optional, for cloning the repository)

## Building the Application

1. Clone the repository (if you haven't already):
   ```
   git clone https://github.com/your-repo/kitchensink-spring.git
   cd kitchensink-spring
   ```

2. Build the application using Maven:
   ```
   mvn clean package
   ```

This will compile the code, run the tests, and create an executable JAR file in the `target` directory.

## Running the Application

You can run the application in several ways:

### Using Maven

```
mvn spring-boot:run
```

### Using the JAR file

```
java -jar target/kitchensink-spring-0.0.1-SNAPSHOT.jar
```

### Using your IDE

Run the `KitchensinkApplication` class as a Java application.

Once started, the application will be available at:
- Base URL: http://localhost:8080/kitchensink
- API Base URL: http://localhost:8080/kitchensink/api
- H2 Console: http://localhost:8080/kitchensink/h2-console (JDBC URL: `jdbc:h2:mem:kitchensink`, Username: `sa`, Password: empty)

## Testing the Application

The project includes both unit tests and integration tests.

### Running all tests

```
mvn test
```

### Running unit tests only

```
mvn test -Dtest=*Test
```

### Running integration tests only

```
mvn test -Dtest=*IntegrationTest
```

## API Documentation

The application provides the following REST endpoints:

### Member API

| Method | URL                           | Description                   | Request Body            | Response                    |
|--------|-------------------------------|-------------------------------|-------------------------|----------------------------- |
| GET    | /api/members                  | Get all members               | -                       | Array of Member objects     |
| GET    | /api/members/{id}             | Get member by ID              | -                       | Member object               |
| GET    | /api/members/email/{email}    | Get member by email           | -                       | Member object               |
| POST   | /api/members                  | Register a new member         | CreateMemberDTO object  | Created Member object       |

### Example Requests

#### Get all members

```
GET http://localhost:8080/kitchensink/api/members
```

#### Get member by ID

```
GET http://localhost:8080/kitchensink/api/members/1
```

#### Get member by email

```
GET http://localhost:8080/kitchensink/api/members/email/john.smith@example.com
```

#### Register a new member

```
POST http://localhost:8080/kitchensink/api/members
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

## Project Structure

```
com.factory.kitchensink
├── config/                  # Configuration classes
├── controller/              # REST controllers
├── dto/                     # Data Transfer Objects
├── event/                   # Event classes
├── exception/               # Custom exceptions and handlers
├── mapper/                  # Object mappers (entity <-> DTO)
├── model/                   # Domain entities
├── repository/              # Spring Data JPA repositories
├── service/                 # Service interfaces
│   └── impl/                # Service implementations
└── KitchensinkApplication.java  # Main application class
```

## Data Model

The application uses a simple data model with a `Member` entity that has the following fields:
- `id`: Long (primary key)
- `name`: String (required, max 25 characters, no numbers)
- `email`: String (required, valid email format)
- `phoneNumber`: String (required, 10-12 digits)

## Development Notes

- The application uses an in-memory H2 database for simplicity
- Sample data is loaded via `data.sql` on startup
- Validation is performed using Jakarta Bean Validation annotations
- Exception handling is centralized in the `GlobalExceptionHandler` class
- The application uses DTOs to separate API representation from internal data model
