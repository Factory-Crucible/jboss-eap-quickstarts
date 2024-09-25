# Kitchensink Spring Boot Migration

## Overview

This project is a migration of the JBoss EAP Quickstarts 'kitchensink' application to a modern Spring Boot architecture. It serves as a demonstration of migrating a Jakarta EE application to the Spring ecosystem while maintaining and enhancing its core functionality.

## Purpose of Migration

The primary goals of this migration are:

1. Modernize the application stack by moving from Java EE 7 to Spring Boot
2. Improve developer productivity and ease of deployment
3. Enhance scalability and maintainability
4. Demonstrate best practices in Spring Boot application development

## Technologies Used

- Java 21
- Spring Boot 3.1.0
- Spring Data JPA
- H2 Database (for development)
- Gradle (build tool)
- JUnit 5 and Mockito (for testing)

## Key Features

- Member registration and management
- RESTful API for CRUD operations on members
- Database persistence using JPA
- Validation of member data
- Comprehensive test suite

## Prerequisites

Before you begin, ensure you have the following installed:
- Java Development Kit (JDK) 21
- Gradle 7.6 or later
- Your favorite IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Quick Start Guide

1. Clone the repository:
   ```
   git clone https://github.com/your-repo/kitchensink-spring-boot.git
   cd kitchensink-spring-boot
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

3. Run the application:
   ```
   ./gradlew bootRun
   ```

4. Access the application:
   Open your web browser and navigate to `http://localhost:8080`

## API Documentation

Once the application is running, you can access the API documentation at:
`http://localhost:8080/swagger-ui.html`

## Running Tests

To run the test suite:
```
./gradlew test
```

## Contributing

We welcome contributions to this project. Please feel free to submit issues and pull requests.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Original JBoss EAP Quickstarts team for the 'kitchensink' application
- Spring Boot team for their excellent framework and documentation

For more detailed information about the migration process and architecture decisions, please refer to the `MIGRATION.md` file in this repository.