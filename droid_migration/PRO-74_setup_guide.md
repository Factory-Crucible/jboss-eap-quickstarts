
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Build and Dependency Management](#build-and-dependency-management)
6. [CI/CD Pipeline Setup](#cicd-pipeline-setup)

## Prerequisites

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- GitHub account (for repository management and CI/CD setup)

## Development Environment Setup

1. Install Java 21 JDK:
   - Visit the official Oracle website or use OpenJDK
   - Download and install Java 21 JDK for your operating system
   - Set JAVA_HOME environment variable:
     - Windows: `setx JAVA_HOME "C:\Program Files\Java\jdk-21"`
     - macOS/Linux: `export JAVA_HOME=/path/to/jdk-21`
   - Add Java to your PATH:
     - Windows: `setx PATH "%PATH%;%JAVA_HOME%\bin"`
     - macOS/Linux: `export PATH=$PATH:$JAVA_HOME/bin`

2. Install Spring Boot CLI:
   - Visit the official Spring Boot website
   - Download the latest stable Spring Boot CLI
   - Extract the archive and add the `bin` directory to your PATH

3. Set up IDE:
   - Download and install your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)
   - Install the following plugins/extensions:
     - Spring Boot Extension Pack
     - Java Extension Pack
     - Maven for Java

4. Verify installations:
   ```
   java --version
   spring --version
   ```

## Project Initialization

1. Clone the existing repository:
   ```
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts
   ```

2. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   cd kitchensink-spring
   ```

3. Initialize a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,security --groupId=org.jboss.as.quickstarts --artifactId=kitchensink-spring --name=kitchensink-spring --package-name=org.jboss.as.quickstarts.kitchensink --description="Kitchensink Spring Boot Migration" .
   ```

4. Open the project in your IDE

## Version Control Setup

1. Initialize Git in the new Spring Boot project directory:
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

4. Stage and commit the new Spring Boot project:
   ```
   git add .
   git commit -m "Initialize Spring Boot project for kitchensink migration"
   ```

## Build and Dependency Management

1. Open `pom.xml` in your IDE

2. Verify that the following dependencies are present:
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
           <artifactId>spring-boot-starter-security</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-devtools</artifactId>
           <scope>runtime</scope>
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

3. Set up Maven Wrapper:
   ```
   mvn -N io.takari:maven:wrapper
   ```

4. Verify the build:
   ```
   ./mvnw clean verify
   ```

## CI/CD Pipeline Setup

1. Create a `.github/workflows` directory in the root of the repository:
   ```
   mkdir -p .github/workflows
   ```

2. Create a `ci-cd.yml` file in the `.github/workflows` directory:
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
         run: mvn clean verify
       - name: Run tests
         run: mvn test
   ```

4. Commit the CI/CD configuration:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD pipeline configuration"
   ```

5. Push the changes to the remote repository:
   ```
   git push origin main
   ```

With these steps completed, you have set up the development environment, initialized the Spring Boot project within the existing repository, configured version control, set up build and dependency management, and created a basic CI/CD pipeline. You are now ready to begin the migration process as outlined in the migration plan.
