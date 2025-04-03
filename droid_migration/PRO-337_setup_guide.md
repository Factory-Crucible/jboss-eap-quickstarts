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
- Gradle 8.x or Maven 3.8+
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

The Spring Boot project will follow the standard Spring Boot structure:

```
kitchensink-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── kitchensink/
│   │   │               ├── controller/    # REST controllers and web controllers
│   │   │               ├── model/         # Domain models
│   │   │               ├── repository/    # Data access layer
│   │   │               ├── service/       # Business logic
│   │   │               ├── exception/     # Custom exceptions
│   │   │               ├── config/        # Application configuration
│   │   │               └── KitchensinkApplication.java
│   │   ├── resources/
│   │   │   ├── static/      # Static resources (CSS, JS, images)
│   │   │   ├── templates/   # Thymeleaf templates
│   │   │   ├── application.properties
│   │   │   └── data.sql     # Initial data
│   └── test/
│       ├── java/            # Test classes
│       └── resources/       # Test resources
├── .gitignore
├── build.gradle or pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 3. Development Environment Setup <a name="development-environment-setup"></a>

1. Install Java 21:
   ```
   # For Ubuntu/Debian
   sudo apt-get update
   sudo apt-get install openjdk-21-jdk
   
   # For macOS with Homebrew
   brew install openjdk@21
   
   # For Windows
   # Download and install from https://adoptium.net/
   ```

2. Verify Java installation:
   ```
   java -version
   ```

3. Install your preferred IDE:
   - **IntelliJ IDEA**: Download from https://www.jetbrains.com/idea/
   - **Eclipse**: Download STS (Spring Tool Suite) from https://spring.io/tools
   - **VS Code**: Install from https://code.visualstudio.com/ with Java and Spring Boot extensions

4. Install Git:
   ```
   # For Ubuntu/Debian
   sudo apt-get install git
   
   # For macOS
   brew install git
   
   # For Windows
   # Download and install from https://git-scm.com/
   ```

5. Install Gradle or Maven:
   ```
   # For Gradle on Ubuntu/Debian
   sudo apt-get install gradle
   
   # For Maven on Ubuntu/Debian
   sudo apt-get install maven
   
   # For macOS
   brew install gradle maven
   
   # For Windows
   # Download and install from their respective websites
   ```

6. Install Docker and Docker Compose:
   ```
   # For Ubuntu/Debian
   sudo apt-get install docker.io docker-compose
   
   # For macOS and Windows
   # Download and install Docker Desktop from https://www.docker.com/products/docker-desktop
   ```

7. Install Kubernetes CLI:
   ```
   # For Ubuntu/Debian
   sudo snap install kubectl --classic
   
   # For macOS
   brew install kubernetes-cli
   
   # For Windows
   # Install via Chocolatey: choco install kubernetes-cli
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
   # Using curl and unzip
   curl https://start.spring.io/starter.tgz \
     -d dependencies=web,data-jpa,h2,validation,lombok,thymeleaf \
     -d type=gradle-project \
     -d bootVersion=3.2.0 \
     -d groupId=com.example \
     -d artifactId=kitchensink \
     -d name=kitchensink \
     -d description="Spring Boot migration of JBoss kitchensink" \
     -d packageName=com.example.kitchensink \
     -d javaVersion=21 \
     -d baseDir=. | tar -xzvf -
   ```

   Alternatively, visit https://start.spring.io/ in your browser, configure the project with the same settings, download the ZIP file, and extract it to the kitchensink-spring-boot directory.

4. Verify the project structure:
   ```
   ls -la
   ```

## 5. Version Control Setup <a name="version-control-setup"></a>

1. Initialize a new Git repository in the kitchensink-spring-boot directory:
   ```
   git init
   ```

2. Create a .gitignore file (if not already created by Spring Initializr):
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
   
   ### Application specific ###
   application-local.properties
   application-local.yml
   *.log
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
   plugins {
       id 'java'
       id 'org.springframework.boot' version '3.2.0'
       id 'io.spring.dependency-management' version '1.1.4'
   }

   group = 'com.example'
   version = '0.0.1-SNAPSHOT'

   java {
       sourceCompatibility = '21'
   }

   configurations {
       compileOnly {
           extendsFrom annotationProcessor
       }
   }

   repositories {
       mavenCentral()
   }

   dependencies {
       implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
       implementation 'org.springframework.boot:spring-boot-starter-web'
       implementation 'org.springframework.boot:spring-boot-starter-validation'
       implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
       compileOnly 'org.projectlombok:lombok'
       runtimeOnly 'com.h2database:h2'
       annotationProcessor 'org.projectlombok:lombok'
       testImplementation 'org.springframework.boot:spring-boot-starter-test'
       testImplementation 'org.springframework.boot:spring-boot-testcontainers'
       testImplementation 'org.testcontainers:junit-jupiter'
   }

   tasks.named('test') {
       useJUnitPlatform()
   }
   ```

If using Maven:

1. Open `pom.xml` and ensure it contains the necessary dependencies:

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
       <parent>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-parent</artifactId>
           <version>3.2.0</version>
           <relativePath/>
       </parent>
       <groupId>com.example</groupId>
       <artifactId>kitchensink</artifactId>
       <version>0.0.1-SNAPSHOT</version>
       <name>kitchensink</name>
       <description>Spring Boot migration of JBoss kitchensink</description>
       
       <properties>
           <java.version>21</java.version>
       </properties>
       
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
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-thymeleaf</artifactId>
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
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-testcontainers</artifactId>
               <scope>test</scope>
           </dependency>
           <dependency>
               <groupId>org.testcontainers</groupId>
               <artifactId>junit-jupiter</artifactId>
               <scope>test</scope>
           </dependency>
       </dependencies>
       
       <build>
           <plugins>
               <plugin>
                   <groupId>org.springframework.boot</groupId>
                   <artifactId>spring-boot-maven-plugin</artifactId>
                   <configuration>
                       <excludes>
                           <exclude>
                               <groupId>org.projectlombok</groupId>
                               <artifactId>lombok</artifactId>
                           </exclude>
                       </excludes>
                   </configuration>
               </plugin>
           </plugins>
       </build>
   </project>
   ```

## 7. Database Setup <a name="database-setup"></a>

For initial development (H2 database):

1. Open `src/main/resources/application.properties` and add:

   ```
   # H2 Database Configuration
   spring.datasource.url=jdbc:h2:mem:kitchensink
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   
   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   
   # Server Configuration
   server.port=8080
   
   # Logging Configuration
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```

2. Create a `src/main/resources/data.sql` file to initialize the database with sample data:

   ```sql
   -- Initial data for Member table
   INSERT INTO member (id, name, email, phone_number) VALUES (1, 'John Smith', 'john.smith@example.com', '2125551212');
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
   # MongoDB Configuration
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

4. Set up TestContainers for integration testing:

   Create a base test class for database tests:
   ```java
   package com.example.kitchensink;

   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.test.context.DynamicPropertyRegistry;
   import org.springframework.test.context.DynamicPropertySource;
   import org.testcontainers.containers.PostgreSQLContainer;
   import org.testcontainers.junit.jupiter.Container;
   import org.testcontainers.junit.jupiter.Testcontainers;

   @SpringBootTest
   @Testcontainers
   public abstract class BaseIntegrationTest {

       @Container
       static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

       @DynamicPropertySource
       static void registerPgProperties(DynamicPropertyRegistry registry) {
           registry.add("spring.datasource.url", postgres::getJdbcUrl);
           registry.add("spring.datasource.username", postgres::getUsername);
           registry.add("spring.datasource.password", postgres::getPassword);
       }
   }
   ```

## 9. CI/CD and Deployment Setup <a name="cicd-and-deployment-setup"></a>

1. Create a Dockerfile in the project root:
   ```
   FROM eclipse-temurin:21-jdk-alpine as build
   WORKDIR /workspace/app

   COPY gradle gradle
   COPY build.gradle settings.gradle gradlew ./
   COPY src src

   RUN ./gradlew build -x test
   RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

   FROM eclipse-temurin:21-jre-alpine
   VOLUME /tmp
   ARG DEPENDENCY=/workspace/app/build/dependency
   COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
   COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
   COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
   ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.kitchensink.KitchensinkApplication"]
   ```

2. Create a docker-compose.yml file:
   ```yaml
   version: '3.8'
   services:
     app:
       build: .
       ports:
         - "8080:8080"
       environment:
         - SPRING_PROFILES_ACTIVE=docker
       depends_on:
         - db
     db:
       image: postgres:14-alpine
       ports:
         - "5432:5432"
       environment:
         - POSTGRES_DB=kitchensink
         - POSTGRES_USER=postgres
         - POSTGRES_PASSWORD=postgres
       volumes:
         - postgres-data:/var/lib/postgresql/data
   
   volumes:
     postgres-data:
   ```

3. For Kubernetes setup, create a deployment.yaml file:
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
           image: kitchensink:latest
           ports:
           - containerPort: 8080
           env:
           - name: SPRING_PROFILES_ACTIVE
             value: "prod"
           resources:
             limits:
               memory: "512Mi"
               cpu: "500m"
             requests:
               memory: "256Mi"
               cpu: "200m"
           readinessProbe:
             httpGet:
               path: /actuator/health/readiness
               port: 8080
             initialDelaySeconds: 20
             periodSeconds: 10
           livenessProbe:
             httpGet:
               path: /actuator/health/liveness
               port: 8080
             initialDelaySeconds: 30
             periodSeconds: 15
   ---
   apiVersion: v1
   kind: Service
   metadata:
     name: kitchensink-service
   spec:
     selector:
       app: kitchensink
     ports:
     - port: 80
       targetPort: 8080
     type: ClusterIP
   ```

4. For CI/CD, create a .github/workflows directory and add a main.yml file:
   ```
   mkdir -p .github/workflows
   touch .github/workflows/main.yml
   ```

5. Add the following content to main.yml for a basic GitHub Actions workflow:
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
       - uses: actions/checkout@v3
       - name: Set up JDK 21
         uses: actions/setup-java@v3
         with:
           java-version: '21'
           distribution: 'temurin'
           cache: gradle
       - name: Build with Gradle
         run: ./gradlew build
       - name: Run tests
         run: ./gradlew test
       - name: Build Docker image
         run: docker build -t kitchensink:latest .
       - name: Save Docker image
         if: github.ref == 'refs/heads/main'
         run: docker save kitchensink:latest > kitchensink.tar
       - name: Upload artifact
         if: github.ref == 'refs/heads/main'
         uses: actions/upload-artifact@v3
         with:
           name: docker-image
           path: kitchensink.tar
   ```

This completes the setup guide for the JBoss 'kitchensink' to Spring Boot migration project. You now have a development environment ready for the migration process, with the necessary tools and configurations in place.
