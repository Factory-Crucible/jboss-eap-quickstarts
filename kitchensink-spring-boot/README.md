# Kitchensink Spring Boot Application

## 1. Project Overview

This project is a modernized version of the JBoss EAP 'kitchensink' quickstart application, migrated to Spring Boot with Java 21. It demonstrates a web-enabled database application using Spring MVC, Spring Data JPA, and Bean Validation.

The application provides functionality for managing members, including:
- Member registration with validation
- Listing all registered members
- RESTful API for CRUD operations on members

This migration showcases how to transition from a Jakarta EE application to a modern Spring Boot application while maintaining the same core functionality.

## 2. Technology Stack

- **Java 21**
- **Spring Boot 3.1.x**
- **Spring Data JPA** - For database access
- **Spring Web** - For REST endpoints and web controllers
- **Spring Validation** - For bean validation
- **H2 Database** - For development and testing
- **MongoDB** (Optional) - Alternative NoSQL database
- **Lombok** - For reducing boilerplate code
- **JUnit 5 & Spring Boot Test** - For testing
- **Docker & Kubernetes** - For containerization and orchestration

### Requirements

- JDK 21
- Gradle 7.x or Maven 3.x
- Docker (for containerization)
- Kubernetes CLI (for deployment)

## 3. Setup and Build Instructions

### Clone the Repository

```bash
git clone https://github.com/your-repository/kitchensink-spring-boot.git
cd kitchensink-spring-boot
```

### Build with Maven

```bash
./mvnw clean package
```

### Build with Gradle

```bash
./gradlew build
```

## 4. Running Instructions

### Run Locally

#### Using Maven

```bash
./mvnw spring-boot:run
```

#### Using Gradle

```bash
./gradlew bootRun
```

#### Using Java

```bash
java -jar target/kitchensink-spring-boot-0.0.1-SNAPSHOT.jar
```

### Access the Application

The application will be running at: http://localhost:8080/

## 5. Testing Instructions

### Run Unit Tests

#### Using Maven

```bash
./mvnw test
```

#### Using Gradle

```bash
./gradlew test
```

### Run Integration Tests

#### Using Maven

```bash
./mvnw verify
```

#### Using Gradle

```bash
./gradlew integrationTest
```

## 6. Database Configurations

### H2 Database (Default)

The application uses an in-memory H2 database by default. The configuration is in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

You can access the H2 console at: http://localhost:8080/h2-console

### MongoDB Configuration (Optional)

To use MongoDB instead of H2, update your `application.properties`:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=kitchensink
```

And enable the MongoDB profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mongodb
```

or

```bash
./gradlew bootRun --args='--spring.profiles.active=mongodb'
```

## 7. Deployment Instructions

### Docker Deployment

#### Build Docker Image

```bash
docker build -t kitchensink-spring-boot:latest .
```

#### Run Docker Container

```bash
docker run -p 8080:8080 kitchensink-spring-boot:latest
```

### Docker Compose

```bash
docker-compose up -d
```

### Kubernetes Deployment

#### Apply Kubernetes Manifests

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```

#### Check Deployment Status

```bash
kubectl get pods
kubectl get services
```

## API Documentation

The REST API is documented using Swagger/OpenAPI and is available at:
http://localhost:8080/swagger-ui.html

## Migration Notes

This application was migrated from a Jakarta EE application running on JBoss EAP to Spring Boot. The migration included:

1. Converting JPA entities to Spring Data JPA
2. Replacing JAX-RS endpoints with Spring REST controllers
3. Replacing CDI with Spring's dependency injection
4. Replacing JSF views with a modern frontend (optional)
5. Updating the testing framework from Arquillian to JUnit 5 with Spring Boot Test

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
