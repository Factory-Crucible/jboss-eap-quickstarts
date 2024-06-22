
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Build and Dependency Management](#build-and-dependency-management)
6. [CI/CD Pipeline Setup](#cicd-pipeline-setup)
7. [Containerization and Kubernetes Setup](#containerization-and-kubernetes-setup)

## 1. Prerequisites <a name="prerequisites"></a>

Ensure you have the following software installed on your development machine:

- Java 21 JDK
- Git
- Maven
- Docker
- Kubernetes CLI (kubectl)
- Your preferred IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Download Java 21 JDK from the official Oracle website or use OpenJDK.
2. Install the JDK following the instructions for your operating system.
3. Set the JAVA_HOME environment variable to point to your Java 21 installation.
4. Add the Java bin directory to your system PATH.

### 2.2 Install Spring Boot CLI

1. Download the Spring Boot CLI from the official Spring website.
2. Extract the archive to a directory of your choice.
3. Add the bin directory of the extracted archive to your system PATH.

### 2.3 IDE Setup

1. Install your preferred IDE (IntelliJ IDEA, Eclipse, or VS Code).
2. Install the following plugins/extensions for your IDE:
   - Spring Boot Extension Pack
   - Java Extension Pack
   - Lombok plugin (if using IntelliJ IDEA)

## 3. Project Initialization <a name="project-initialization"></a>

### 3.1 Create New Project Structure

1. Open a terminal and navigate to the root of the existing repository.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-boot-kitchensink
   ```
3. Move the existing JBoss project into a subdirectory:
   ```
   mkdir jboss-kitchensink-original
   git mv * jboss-kitchensink-original/
   git mv .* jboss-kitchensink-original/
   ```
   Note: This command may not move hidden files correctly. Ensure all files are moved manually if necessary.

### 3.2 Initialize Spring Boot Project

1. Navigate to the `spring-boot-kitchensink` directory:
   ```
   cd spring-boot-kitchensink
   ```
2. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,lombok --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink --description="Migrated Kitchensink Application" .
   ```

## 4. Version Control Setup <a name="version-control-setup"></a>

1. In the root directory of the repository, update the `.gitignore` file to include Spring Boot specific entries:
   ```
   echo "HELP.md
   target/
   !.mvn/wrapper/maven-wrapper.jar
   !**/src/main/**/target/
   !**/src/test/**/target/

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

   ### NetBeans ###
   /nbproject/private/
   /nbbuild/
   /dist/
   /nbdist/
   /.nb-gradle/
   build/
   !**/src/main/**/build/
   !**/src/test/**/build/

   ### VS Code ###
   .vscode/
   " >> .gitignore
   ```

2. Stage and commit the changes:
   ```
   git add .
   git commit -m "Set up project structure for Spring Boot migration"
   ```

## 5. Build and Dependency Management <a name="build-and-dependency-management"></a>

1. Navigate to the `spring-boot-kitchensink` directory.

2. Open the `pom.xml` file and ensure it contains the necessary Spring Boot dependencies:
   ```xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-data-jpa</artifactId>
       </dependency>
       <dependency>
           <groupId>com.h2database</groupId>
           <artifactId>h2</artifactId>
           <scope>runtime</scope>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-devtools</artifactId>
           <scope>runtime</scope>
           <optional>true</optional>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-actuator</artifactId>
       </dependency>
       <dependency>
           <groupId>org.projectlombok</groupId>
           <artifactId>lombok</artifactId>
           <optional>true</optional>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-test</artifactId>
           <scope>test</scope>
       </dependency>
   </dependencies>
   ```

3. Set up the Maven wrapper:
   ```
   mvn wrapper:wrapper
   ```

## 6. CI/CD Pipeline Setup <a name="cicd-pipeline-setup"></a>

1. In the root directory of the repository, create a `.github/workflows` directory:
   ```
   mkdir -p .github/workflows
   ```

2. Create a new file named `ci-cd.yml` in the `.github/workflows` directory:
   ```
   touch .github/workflows/ci-cd.yml
   ```

3. Add the following content to `ci-cd.yml`:
   ```yaml
   name: CI/CD Pipeline

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
       - name: Build with Maven
         run: |
           cd spring-boot-kitchensink
           ./mvnw clean install
   ```

4. Commit the changes:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add basic CI/CD pipeline"
   ```

## 7. Containerization and Kubernetes Setup <a name="containerization-and-kubernetes-setup"></a>

### 7.1 Dockerfile Creation

1. In the `spring-boot-kitchensink` directory, create a `Dockerfile`:
   ```
   touch Dockerfile
   ```

2. Add the following content to the `Dockerfile`:
   ```dockerfile
   FROM openjdk:21-jdk-slim
   VOLUME /tmp
   ARG JAR_FILE=target/*.jar
   COPY ${JAR_FILE} app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

### 7.2 Docker Compose Setup

1. In the `spring-boot-kitchensink` directory, create a `docker-compose.yml` file:
   ```
   touch docker-compose.yml
   ```

2. Add the following content to `docker-compose.yml`:
   ```yaml
   version: '3'
   services:
     app:
       build: .
       ports:
         - "8080:8080"
   ```

### 7.3 Kubernetes Configuration

1. In the `spring-boot-kitchensink` directory, create a `kubernetes` folder:
   ```
   mkdir kubernetes
   ```

2. Create a `deployment.yaml` file in the `kubernetes` folder:
   ```
   touch kubernetes/deployment.yaml
   ```

3. Add the following content to `deployment.yaml`:
   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: kitchensink-app
   spec:
     replicas: 1
     selector:
       matchLabels:
         app: kitchensink-app
     template:
       metadata:
         labels:
           app: kitchensink-app
       spec:
         containers:
         - name: kitchensink-app
           image: kitchensink-app:latest
           ports:
           - containerPort: 8080
   ```

4. Create a `service.yaml` file in the `kubernetes` folder:
   ```
   touch kubernetes/service.yaml
   ```

5. Add the following content to `service.yaml`:
   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: kitchensink-app-service
   spec:
     selector:
       app: kitchensink-app
     ports:
       - protocol: TCP
         port: 80
         targetPort: 8080
     type: LoadBalancer
   ```

6. Commit the changes:
   ```
   git add Dockerfile docker-compose.yml kubernetes/
   git commit -m "Add Docker and Kubernetes configurations"
   ```

This completes the setup guide for the JBoss 'kitchensink' to Spring Boot migration project. You now have a development environment ready for the migration process, with the necessary tools, configurations, and project structure in place.
