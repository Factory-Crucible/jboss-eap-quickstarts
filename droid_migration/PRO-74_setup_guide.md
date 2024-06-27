
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Development Environment Setup](#development-environment-setup)
4. [Project Initialization](#project-initialization)
5. [Version Control Setup](#version-control-setup)
6. [Build and Dependency Management](#build-and-dependency-management)
7. [Application Configuration](#application-configuration)
8. [Next Steps](#next-steps)

## 1. Introduction <a name="introduction"></a>

This guide provides step-by-step instructions for setting up your development environment and initializing the project structure for migrating the JBoss 'kitchensink' application to Spring Boot. The setup process will prepare your environment for the actual migration work outlined in the migration plan.

## 2. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:

- Administrative access to your development machine
- Internet connection
- Basic familiarity with command-line operations

## 3. Development Environment Setup <a name="development-environment-setup"></a>

### 3.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Download the appropriate JDK 21 installer for your operating system.
3. Run the installer and follow the installation wizard.
4. Set the JAVA_HOME environment variable:
   - Windows: Set JAVA_HOME to the JDK installation directory (e.g., C:\Program Files\Java\jdk-21)
   - macOS/Linux: Add `export JAVA_HOME=/path/to/jdk-21` to your ~/.bash_profile or ~/.zshrc
5. Add Java to your PATH:
   - Windows: Add %JAVA_HOME%\bin to your PATH
   - macOS/Linux: Add `export PATH=$JAVA_HOME/bin:$PATH` to your profile
6. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 3.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page: https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli
2. Follow the installation instructions for your operating system.
3. Verify the installation by running:
   ```
   spring --version
   ```

### 3.3 Set Up IDE with Spring Boot Support

We recommend using IntelliJ IDEA for this project:

1. Download and install IntelliJ IDEA Ultimate Edition from: https://www.jetbrains.com/idea/download/
2. During installation, ensure you select the option to include Spring Boot support.
3. Launch IntelliJ IDEA and go to File > Settings > Plugins.
4. Search for and install the following plugins if not already installed:
   - Spring Boot
   - Java
   - Maven
   - Git Integration

## 4. Project Initialization <a name="project-initialization"></a>

### 4.1 Clone the Existing Repository

1. Open a terminal and navigate to your preferred workspace directory.
2. Clone the existing repository:
   ```
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts
   ```

### 4.2 Create New Project Structure

1. Inside the `jboss-eap-quickstarts` directory, create a new folder for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   cd kitchensink-spring
   ```

### 4.3 Initialize Spring Boot Project

1. Visit https://start.spring.io/ in your web browser.
2. Configure the project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Latest stable version
   - Project Metadata:
     - Group: org.jboss.as.quickstarts
     - Artifact: kitchensink-spring
     - Name: kitchensink-spring
     - Description: Kitchensink quickstart migrated to Spring Boot
     - Package name: org.jboss.as.quickstarts.kitchensink
     - Packaging: Jar
     - Java: 21
   - Dependencies: Add the following
     - Spring Web
     - Spring Data JPA
     - H2 Database
     - Spring Boot DevTools
     - Spring Boot Actuator
     - Lombok
3. Click "Generate" to download the project zip file.
4. Extract the contents of the zip file into the `kitchensink-spring` directory.

## 5. Version Control Setup <a name="version-control-setup"></a>

1. In the `kitchensink-spring` directory, initialize a new Git repository:
   ```
   git init
   ```
2. Create a `.gitignore` file:
   ```
   touch .gitignore
   ```
3. Add the following content to `.gitignore`:
   ```
   HELP.md
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
   ```
4. Stage and commit the initial project structure:
   ```
   git add .
   git commit -m "Initial Spring Boot project structure"
   ```

## 6. Build and Dependency Management <a name="build-and-dependency-management"></a>

1. Open the `pom.xml` file in the `kitchensink-spring` directory.
2. Review the dependencies and ensure they match the ones selected during project initialization.
3. Add any additional dependencies required for the migration (e.g., validation API).
4. Save the `pom.xml` file.

## 7. Application Configuration <a name="application-configuration"></a>

1. Open `src/main/resources/application.properties`.
2. Add the following initial configuration:
   ```
   # Server port
   server.port=8080

   # H2 Database configuration
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

   # JPA/Hibernate properties
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # Logging
   logging.level.org.springframework=INFO
   logging.level.org.jboss.as.quickstarts.kitchensink=DEBUG
   ```
3. Save the file.

## 8. Next Steps <a name="next-steps"></a>

With the development environment set up and the project initialized, you are now ready to begin the actual migration process as outlined in the migration plan. The next steps involve:

1. Migrating the domain model
2. Implementing the data access layer
3. Migrating business logic
4. Creating REST API endpoints
5. Implementing security and validation

Remember to commit your changes regularly and follow the migration plan closely. Good luck with your migration project!
