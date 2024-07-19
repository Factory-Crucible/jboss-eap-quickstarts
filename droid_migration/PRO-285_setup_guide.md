
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Project Structure Configuration](#project-structure-configuration)
6. [Application Properties Configuration](#application-properties-configuration)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- Basic familiarity with command-line operations

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page or use OpenJDK.
2. Download and install Java 21 JDK for your operating system.
3. Set the JAVA_HOME environment variable:
   - On Windows: Set JAVA_HOME in System Properties > Environment Variables
   - On macOS/Linux: Add `export JAVA_HOME=/path/to/jdk21` to your shell profile

4. Verify the installation:
   ```
   java -version
   ```

### 2.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page.
2. Download the latest stable version.
3. Extract the archive and add the `bin` directory to your PATH.
4. Verify the installation:
   ```
   spring --version
   ```

### 2.3 Set up IDE

We recommend using IntelliJ IDEA or Eclipse STS (Spring Tool Suite) for Spring Boot development.

For IntelliJ IDEA:
1. Download and install IntelliJ IDEA (Community or Ultimate edition).
2. Open IntelliJ IDEA and go to Plugins.
3. Search for and install the "Spring Boot" plugin.

For Eclipse STS:
1. Download Eclipse STS from the official website.
2. Install Eclipse STS, which comes pre-configured for Spring Boot development.

## 3. Project Initialization <a name="project-initialization"></a>

### 3.1 Create a New Spring Boot Project

1. Open your terminal or command prompt.
2. Navigate to the root directory of your existing JBoss project.
3. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-kitchensink
   cd spring-kitchensink
   ```
4. Use Spring Initializr to create a new project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,validation,lombok,devtools,actuator --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink
   ```

### 3.2 Import Project into IDE

For IntelliJ IDEA:
1. Open IntelliJ IDEA.
2. Click "Open" and select the `spring-kitchensink` directory.
3. Wait for IntelliJ to import and index the project.

For Eclipse STS:
1. Open Eclipse STS.
2. Go to File > Import > Maven > Existing Maven Projects.
3. Browse to the `spring-kitchensink` directory and select it.
4. Click Finish and wait for Eclipse to import the project.

## 4. Version Control Setup <a name="version-control-setup"></a>

1. Open a terminal and navigate to the root of your existing JBoss project.
2. Ensure you're on the main branch of your existing repository:
   ```
   git checkout main
   ```
3. Create a new branch for the migration:
   ```
   git checkout -b spring-boot-migration
   ```
4. Add the new Spring Boot project to version control:
   ```
   git add spring-kitchensink
   git commit -m "Initialize Spring Boot project for migration"
   ```

## 5. Project Structure Configuration <a name="project-structure-configuration"></a>

1. In your IDE, navigate to the `spring-kitchensink/src/main/java/com/example/kitchensink` directory.
2. Create the following package structure:
   - `controller`
   - `data`
   - `model`
   - `service`
   - `util`

3. In each package, create a placeholder file (e.g., `.gitkeep`) to ensure the directory structure is committed to version control.

## 6. Application Properties Configuration <a name="application-properties-configuration"></a>

1. Open `spring-kitchensink/src/main/resources/application.properties`.
2. Add the following initial configuration:

   ```properties
   # Server port
   server.port=8080

   # H2 Database configuration
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

   # Enable H2 console
   spring.h2.console.enabled=true

   # JPA/Hibernate properties
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # Logging
   logging.level.org.springframework=INFO
   logging.level.com.example.kitchensink=DEBUG

   # Actuator
   management.endpoints.web.exposure.include=*
   management.endpoint.health.show-details=always
   ```

3. Save the file.

With these steps completed, your development environment is now set up and ready for the migration process. The new Spring Boot project is initialized within your existing repository, allowing you to maintain both codebases during the migration. You can now proceed with the actual migration steps as outlined in the migration plan.
