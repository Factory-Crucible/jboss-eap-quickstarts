# Kitchensink Spring Boot Migration

This project is a modernized version of the JBoss EAP 'kitchensink' quickstart application, migrated to Spring Boot with Java 21. It demonstrates how to build a modern, cloud-ready application using Spring Boot while preserving the functionality of the original JBoss application.

## Table of Contents

- [Project Overview](#project-overview)
- [Migration Details](#migration-details)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Deployment](#deployment)
- [MongoDB Integration](#mongodb-integration)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)

## Project Overview

The original JBoss 'kitchensink' application is a Jakarta EE application that demonstrates CDI, JPA, JAX-RS, and Bean Validation. This Spring Boot version maintains the same functionality while leveraging modern Spring Boot features and Java 21 capabilities.

### Key Features

- Member registration and management
- RESTful API for member data
- Data validation
- Persistence with JPA (and optional MongoDB)
- Modern, responsive UI
- Comprehensive test suite
- Cloud-ready deployment configuration

## Migration Details

This project represents a complete migration from JBoss EAP to Spring Boot, including:

- **Framework Migration**: Jakarta EE → Spring Boot 3.2.3
- **Java Version**: Java EE → Java 21
- **Build System**: Maven → Gradle
- **Database**: H2 (with optional MongoDB support)
- **UI Layer**: JSF → Spring MVC with Thymeleaf
- **REST API**: JAX-RS → Spring REST Controllers
- **Validation**: Bean Validation → Spring Validation
- **Testing**: Arquillian → JUnit 5 with Spring Boot Test

## Project Structure

```
kitchensink-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── kitchensink/
│   │   │               ├── controller/    # REST and MVC controllers
│   │   │               ├── model/         # Domain model classes
│   │   │               ├── repository/    # Data access repositories
│   │   │               │   ├── jpa/       # JPA repositories
│   │   │               │   └── mongo/     # MongoDB repositories (optional)
│   │   │               ├── service/       # Business logic services
│   │   │               ├── exception/     # Custom exceptions and handlers
│   │   │               ├── config/        # Application configuration
│   │   │               └── KitchensinkApplication.java  # Main class
│   │   └── resources/
│   │       ├── static/              # Static resources (CSS, JS)
│   │       ├── templates/           # Thymeleaf templates
│   │       └── application.yml      # Application configuration
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── kitchensink/
│                       ├── controller/    # Controller tests
│                       ├── repository/    # Repository tests
│                       ├── service/       # Service tests
│                       └── integration/   # Integration tests
├── build.gradle                   # Gradle build configuration
├── Dockerfile                     # Docker configuration
├── docker-compose.yml             # Docker Compose configuration
├── .gitignore                     # Git ignore rules
└── README.md                      # This file
```

## Prerequisites

- Java 21 JDK
- Gradle 7.6+ or Maven 3.8+
- Docker and Docker Compose (for containerized development)
- Git
- MongoDB (optional, for the MongoDB integration)

## Setup Instructions

### Clone the Repository

```bash
git clone https://github.com/your-organization/jboss-eap-quickstarts.git
cd jboss-eap-quickstarts/kitchensink-spring-boot
```

### Build the Project

Using Gradle:
```bash
./gradlew build
```

Using Maven:
```bash
./mvnw clean package
```

### Configure the Database

The application is configured to use an H2 in-memory database by default. If you want to use MongoDB:

1. Ensure MongoDB is installed and running
2. Update the MongoDB connection properties in `application.yml` if needed
3. Enable the MongoDB profile when running the application

## Running the Application

### Run Locally

```bash
./gradlew bootRun
```

Or with Maven:
```bash
./mvnw spring-boot:run
```

### Run with Docker

```bash
docker-compose up
```

### Access the Application

Once running, the application will be available at:

- Web UI: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (if enabled)
- REST API: http://localhost:8080/api/members

## Testing

### Run Tests

```bash
./gradlew test
```

### Run Integration Tests

```bash
./gradlew integrationTest
```

## Deployment

### Build Docker Image

```bash
docker build -t kitchensink:latest .
```

### Deploy to Kubernetes

```bash
kubectl apply -f kubernetes/deployment.yaml
```

## MongoDB Integration

This project includes optional MongoDB integration as a stretch goal. To use MongoDB instead of JPA:

1. Ensure MongoDB is running
2. Enable the MongoDB profile:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=mongodb'
   ```
3. The application will automatically use MongoDB repositories instead of JPA

## API Documentation

The REST API endpoints are:

- `GET /api/members` - List all members
- `GET /api/members/{id}` - Get a specific member
- `POST /api/members` - Create a new member
- `PUT /api/members/{id}` - Update a member
- `DELETE /api/members/{id}` - Delete a member

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
