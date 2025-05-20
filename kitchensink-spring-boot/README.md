# Kitchensink Spring Boot

## Introduction

This project is a migration of the JBoss EAP Kitchensink quickstart application to a modern Spring Boot application using Java 21. The original Kitchensink application demonstrates Jakarta EE technologies including CDI, JPA, and JAX-RS. This Spring Boot version maintains the same functionality while leveraging Spring's modern architecture and capabilities.

## Purpose

The purpose of this migration is to:
- Modernize the application architecture
- Take advantage of Spring Boot's simplified configuration and deployment
- Utilize Java 21 features
- Improve maintainability and scalability
- Demonstrate migration patterns from Jakarta EE to Spring Boot

## Prerequisites

To build and run this application, you'll need:

- JDK 21
- Gradle 7.x or Maven 3.x
- Git
- Docker and Docker Compose (for containerized deployment)
- Kubernetes CLI (kubectl) (for Kubernetes deployment)

## Building the Application

### Using Gradle

```bash
./gradlew build
```

### Using Maven

```bash
./mvnw clean package
```

## Running the Application

### Local Development

```bash
# Using Gradle
./gradlew bootRun

# Using Maven
./mvnw spring-boot:run
```

The application will be available at http://localhost:8080

### Using Docker

```bash
# Build the Docker image
docker build -t kitchensink-spring-boot .

# Run the container
docker run -p 8080:8080 kitchensink-spring-boot
```

### Using Docker Compose

```bash
docker-compose up
```

### Using Kubernetes

```bash
kubectl apply -f deployment.yaml
```

## Project Structure

```
kitchensink-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/kitchensink/
│   │   │       ├── controller/     # REST controllers
│   │   │       ├── model/          # Domain entities
│   │   │       ├── repository/     # Data access layer
│   │   │       ├── service/        # Business logic
│   │   │       └── KitchensinkApplication.java
│   │   └── resources/
│   │       ├── application.properties  # Application configuration
│   │       └── static/                 # Static resources
│   └── test/
│       └── java/
│           └── com/example/kitchensink/ # Test classes
├── Dockerfile                  # Docker configuration
├── docker-compose.yml          # Docker Compose configuration
├── deployment.yaml             # Kubernetes deployment configuration
├── build.gradle or pom.xml     # Build configuration
└── README.md                   # This file
```

## Migration Overview

### What Was Migrated

| JBoss EAP Component | Spring Boot Equivalent |
|---------------------|------------------------|
| CDI (Contexts and Dependency Injection) | Spring IoC Container |
| EJB (Enterprise JavaBeans) | Spring @Service components |
| JPA (Java Persistence API) | Spring Data JPA |
| JAX-RS (Java API for RESTful Web Services) | Spring Web (RestController) |
| Bean Validation | Spring Validation |
| JSF (JavaServer Faces) | Thymeleaf templates (optional) |
| Arquillian tests | Spring Boot Test |

### Key Changes

1. **Project Structure**: Reorganized to follow Spring Boot conventions
2. **Dependency Management**: Switched from JBoss dependencies to Spring Boot starters
3. **Configuration**: Replaced XML configurations with application.properties and annotations
4. **Data Access**: Replaced JPA repositories with Spring Data JPA repositories
5. **REST API**: Replaced JAX-RS with Spring Web annotations
6. **Dependency Injection**: Replaced CDI with Spring's IoC container
7. **Testing**: Replaced Arquillian with Spring Boot Test

## Technologies Used

- Spring Boot 3.1.0
- Spring Data JPA
- Spring Web
- Spring Validation
- H2 Database (for development)
- Lombok (for reducing boilerplate code)
- JUnit 5 and Mockito (for testing)
- Docker and Kubernetes (for deployment)

## API Endpoints

The application exposes the following REST endpoints:

- `GET /api/members`: Retrieve all members
- `GET /api/members/{id}`: Retrieve a specific member by ID
- `POST /api/members`: Create a new member
- `PUT /api/members/{id}`: Update an existing member
- `DELETE /api/members/{id}`: Delete a member

## Deployment

### Docker

The application includes a Dockerfile for containerization. The Docker image is built on OpenJDK 21 and exposes port 8080.

### Kubernetes

A Kubernetes deployment configuration is provided in `deployment.yaml`. This configuration deploys the application with one replica and exposes it through a service.

## CI/CD

The project includes GitHub Actions workflows for continuous integration and deployment. The workflows build the application, run tests, and can be configured to deploy to various environments.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
