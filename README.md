# Kitchensink Spring Boot Migration

This project is a migration of the JBoss EAP "kitchensink" quickstart application to Spring Boot. It demonstrates how to convert a Jakarta EE application to a modern Spring Boot application while preserving all functionality.

## Overview

The original JBoss EAP "kitchensink" application was built using Jakarta EE technologies such as CDI, JPA, JAX-RS, and Bean Validation. This migrated version uses Spring Boot and its ecosystem to provide the same functionality with a more modern approach.

## Migrated Components

The following components have been migrated from the original JBoss EAP application:

1. **Domain Model**
   - Migrated the `Member` entity with all its validation constraints
   - Preserved JPA annotations and validation rules

2. **Data Access Layer**
   - Converted CDI-based repositories to Spring Data JPA repositories
   - Simplified data access with Spring Data's automatic implementation

3. **Service Layer**
   - Migrated EJB services to Spring services
   - Replaced CDI events with Spring's ApplicationEventPublisher

4. **REST API**
   - Converted JAX-RS endpoints to Spring REST controllers
   - Implemented equivalent validation and error handling

5. **Exception Handling**
   - Created a global exception handler for consistent error responses
   - Preserved all validation and business logic exceptions

6. **Configuration**
   - Replaced JBoss/Jakarta EE configuration with Spring Boot properties
   - Configured H2 database for development and testing

## Prerequisites

To build and run this application, you need:

- Java 17 or later
- Maven 3.8 or later
- Git (optional, for cloning the repository)

## Building and Running the Application

### Building the Application

```bash
mvn clean package
```

### Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR file directly:

```bash
java -jar target/kitchensink-0.0.1-SNAPSHOT.jar
```

The application will be available at: http://localhost:8080/kitchensink

### Accessing the H2 Console

The H2 database console is available at: http://localhost:8080/kitchensink/h2-console

Connection details:
- JDBC URL: `jdbc:h2:mem:kitchensinkdb`
- Username: `sa`
- Password: `password`

## API Endpoints

The REST API provides the following endpoints:

- `GET /api/members` - List all members
- `GET /api/members/{id}` - Get a member by ID
- `POST /api/members` - Create a new member

Example request to create a member:

```json
POST /api/members
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

## Key Differences from JBoss EAP Implementation

### Architecture Changes

1. **Dependency Injection**
   - JBoss EAP: Uses CDI (Context and Dependency Injection)
   - Spring Boot: Uses Spring's DI container

2. **Transaction Management**
   - JBoss EAP: Uses EJB annotations (`@Stateless`) for transaction demarcation
   - Spring Boot: Uses `@Transactional` annotations

3. **Event System**
   - JBoss EAP: Uses CDI events and observers
   - Spring Boot: Uses Spring's ApplicationEventPublisher and event listeners

4. **REST Implementation**
   - JBoss EAP: Uses JAX-RS (Jakarta RESTful Web Services)
   - Spring Boot: Uses Spring MVC and RestControllers

5. **Configuration**
   - JBoss EAP: Uses XML configuration files (persistence.xml, beans.xml)
   - Spring Boot: Uses application.properties/YAML and Java-based configuration

### Benefits of Migration

1. **Simplified Configuration**
   - Reduced XML configuration
   - Auto-configuration of many components

2. **Embedded Server**
   - No need for a separate application server
   - Simplified deployment and testing

3. **Rich Ecosystem**
   - Access to Spring Boot's vast ecosystem of starters and libraries
   - Better integration with modern development tools

4. **Improved Development Experience**
   - Faster startup times
   - Hot reloading with DevTools
   - Simplified testing

## Testing

The application includes both unit and integration tests:

- Unit tests for services and controllers
- Integration tests for the REST API

Run tests with:

```bash
mvn test
```

## License

This project is licensed under the Apache License 2.0 - see the original JBoss EAP quickstarts repository for details.
