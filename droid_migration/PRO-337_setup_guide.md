
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Development Environment Setup](#development-environment-setup)
4. [Spring Boot Project Initialization](#spring-boot-project-initialization)
5. [Version Control Setup](#version-control-setup)
6. [Build Tool Configuration](#build-tool-configuration)
7. [Database Setup](#database-setup)
8. [Testing Framework Setup](#testing-framework-setup)
9. [CI/CD and Deployment Setup](#cicd-and-deployment-setup)

## 1. Prerequisites <a name="prerequisites"></a>

Ensure you have the following software installed:

- Java Development Kit (JDK) 21
- Git
- Gradle 7.x or Maven 3.x
- Docker and Docker Compose
- Kubernetes CLI (kubectl)
- Your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)

## 2. Project Structure <a name="project-structure"></a>

We'll create a new directory for the Spring Boot project within the existing repository:

```
jboss-eap-quickstarts/
├── kitchensink/              # Original JBoss project
└── kitchensink-spring-boot/  # New Spring Boot project
```

## 3. Development Environment Setup <a name="development-environment-setup"></a>

1. Install Java 21:
   ```
   sudo apt-get update
   sudo apt-get install openjdk-21-jdk
   ```

2. Verify Java installation:
   ```
   java -version
   ```

3. Install your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)

4. Install Git:
   ```
   sudo apt-get install git
   ```

5. Install Gradle or Maven (if not already installed):
   ```
   sudo apt-get install gradle
   # or
   sudo apt-get install maven
   ```

6. Install Docker and Docker Compose:
   ```
   sudo apt-get install docker.io docker-compose
   ```

7. Install Kubernetes CLI:
   ```
   sudo snap install kubectl --classic
   ```

## 4. Spring Boot Project Initialization <a name="spring-boot-project-initialization"></a>

1. Navigate to the jboss-eap-quickstarts directory:
   ```
   cd jboss-eap-quickstarts
   ```

2. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring-boot
   cd kitchensink-spring-boot
   ```

3. Use Spring Initializr to create a new Spring Boot project:
   ```
   curl https://start.spring.io/starter.tgz -d dependencies=web,data-jpa,h2,validation,lombok -d type=gradle-project -d bootVersion=3.1.0 -d baseDir=. | tar -xzvf -
   ```

   This command creates a new Spring Boot project with the following dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Validation
   - Lombok

## 5. Version Control Setup <a name="version-control-setup"></a>

1. Initialize a new Git repository in the kitchensink-spring-boot directory:
   ```
   git init
   ```

2. Create a .gitignore file:
   ```
   touch .gitignore
   ```

3. Add the following content to .gitignore:
   ```
   HELP.md
   .gradle
   build/
   !gradle/wrapper/gradle-wrapper.jar
   !**/src/main/**/build/
   !**/src/test/**/build/

   ### STS ###
   .apt_generated
   .classpath
   .factorypath
   .project
   .settings
   .springBeans
   .sts4-cache

   ### IntelliJ IDEA ###
   .idea
   *.iws
   *.iml
   *.ipr
   out/
   !**/src/main/**/out/
   !**/src/test/**/out/

   ### NetBeans ###
   /nbproject/private/
   /nbbuild/
   /dist/
   /nbdist/
   /.nb-gradle/

   ### VS Code ###
   .vscode/
   ```

4. Make the initial commit:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 6. Build Tool Configuration <a name="build-tool-configuration"></a>

If using Gradle (default):

1. Open `build.gradle` and ensure it contains the necessary dependencies:

   ```groovy
   dependencies {
       implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
       implementation 'org.springframework.boot:spring-boot-starter-web'
       implementation 'org.springframework.boot:spring-boot-starter-validation'
       compileOnly 'org.projectlombok:lombok'
       runtimeOnly 'com.h2database:h2'
       annotationProcessor 'org.projectlombok:lombok'
       testImplementation 'org.springframework.boot:spring-boot-starter-test'
   }
   ```

If using Maven:

1. Open `pom.xml` and ensure it contains the necessary dependencies:

   ```xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-data-jpa</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-validation</artifactId>
       </dependency>
       <dependency>
           <groupId>org.projectlombok</groupId>
           <artifactId>lombok</artifactId>
           <optional>true</optional>
       </dependency>
       <dependency>
           <groupId>com.h2database</groupId>
           <artifactId>h2</artifactId>
           <scope>runtime</scope>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-test</artifactId>
           <scope>test</scope>
       </dependency>
   </dependencies>
   ```

## 7. Database Setup <a name="database-setup"></a>

For initial development (H2 database):

1. Open `src/main/resources/application.properties` and add:

   ```
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true
   ```

For MongoDB setup (optional):

1. Add MongoDB dependency to `build.gradle` or `pom.xml`:

   Gradle:
   ```groovy
   implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
   ```

   Maven:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-mongodb</artifactId>
   </dependency>
   ```

2. Update `application.properties` for MongoDB:

   ```
   spring.data.mongodb.host=localhost
   spring.data.mongodb.port=27017
   spring.data.mongodb.database=kitchensink
   ```

## 8. Testing Framework Setup <a name="testing-framework-setup"></a>

1. JUnit 5 and Mockito are included by default with Spring Boot Starter Test.

2. Create a test directory structure:
   ```
   mkdir -p src/test/java/com/example/kitchensink
   ```

3. Create a sample test class to verify the setup:
   ```java
   package com.example.kitchensink;

   import org.junit.jupiter.api.Test;
   import org.springframework.boot.test.context.SpringBootTest;

   @SpringBootTest
   class KitchensinkApplicationTests {

       @Test
       void contextLoads() {
       }

   }
   ```

## 9. CI/CD and Deployment Setup <a name="cicd-and-deployment-setup"></a>

1. Create a Dockerfile in the project root:
   ```
   touch Dockerfile
   ```

2. Add the following content to the Dockerfile:
   ```dockerfile
   FROM openjdk:21-jdk-slim
   VOLUME /tmp
   ARG JAR_FILE=target/*.jar
   COPY ${JAR_FILE} app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

3. Create a docker-compose.yml file:
   ```
   touch docker-compose.yml
   ```

4. Add the following content to docker-compose.yml:
   ```yaml
   version: '3'
   services:
     app:
       build: .
       ports:
         - "8080:8080"
     mongodb:
       image: mongo:latest
       ports:
         - "27017:27017"
   ```

5. For Kubernetes setup, create a deployment.yaml file:
   ```
   touch deployment.yaml
   ```

6. Add the following content to deployment.yaml:
   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: kitchensink-app
   spec:
     replicas: 1
     selector:
       matchLabels:
         app: kitchensink
     template:
       metadata:
         labels:
           app: kitchensink
       spec:
         containers:
         - name: kitchensink
           image: your-docker-registry/kitchensink:latest
           ports:
           - containerPort: 8080
   ```

7. For CI/CD, create a .github/workflows directory and add a main.yml file:
   ```
   mkdir -p .github/workflows
   touch .github/workflows/main.yml
   ```

8. Add the following content to main.yml for a basic GitHub Actions workflow:
   ```yaml
   name: CI/CD

   on:
     push:
       branches: [ main ]
     pull_request:
       branches: [ main ]

   jobs:
     build:
       runs-on: ubuntu-latest

       steps:
       - uses: actions/checkout@v2
       - name: Set up JDK 21
         uses: actions/setup-java@v2
         with:
           java-version: '21'
           distribution: 'adopt'
       - name: Build with Gradle
         run: ./gradlew build
       - name: Run tests
         run: ./gradlew test
   ```

This completes the setup guide for the JBoss 'kitchensink' to Spring Boot migration project. You now have a development environment ready for the migration process, with the necessary tools and configurations in place.
