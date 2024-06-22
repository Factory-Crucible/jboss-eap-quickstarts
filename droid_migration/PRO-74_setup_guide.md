
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
- GitHub account (for CI/CD setup)

## Development Environment Setup

### 1. Install Java 21 JDK

a. Visit the official Oracle JDK download page or use an open-source alternative like AdoptOpenJDK.
b. Download the Java 21 JDK installer for your operating system.
c. Run the installer and follow the installation wizard.
d. Set the JAVA_HOME environment variable:
   - Windows: Set JAVA_HOME to the JDK installation directory
   - macOS/Linux: Add `export JAVA_HOME=/path/to/jdk21` to your ~/.bash_profile or ~/.zshrc

e. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 2. Install Spring Boot CLI

a. Visit the Spring Boot CLI installation page.
b. Follow the instructions for your operating system to download and install Spring Boot CLI.
c. Verify the installation by running:
   ```
   spring --version
   ```

### 3. Install Git

a. Visit the official Git website and download the installer for your operating system.
b. Run the installer and follow the installation wizard.
c. Verify the installation by running:
   ```
   git --version
   ```

### 4. Install an IDE (Optional but recommended)

Choose and install an IDE with Spring Boot support, such as IntelliJ IDEA or Eclipse.

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

3. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,lombok,validation --groupId=org.jboss.as.quickstarts --artifactId=kitchensink-spring --name=kitchensink-spring --package-name=org.jboss.as.quickstarts.kitchensink --description="Kitchensink Spring Boot Migration" .
   ```

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

4. Stage and commit the initial Spring Boot project:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## Build and Dependency Management

1. Review the generated `pom.xml` file and ensure it contains the necessary dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Boot DevTools
   - Lombok
   - Validation

2. Add any additional dependencies required for the project.

3. Set up the Maven wrapper for a consistent build environment:
   ```
   mvn -N io.takari:maven:wrapper
   ```

4. Verify the build:
   ```
   ./mvnw clean install
   ```

## CI/CD Pipeline Setup

1. Create a `.github/workflows` directory in the root of the repository:
   ```
   mkdir -p .github/workflows
   ```

2. Create a YAML file for the GitHub Actions workflow:
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
         run: mvn -B package --file kitchensink-spring/pom.xml
       - name: Run tests
         run: mvn test --file kitchensink-spring/pom.xml
   ```

4. Commit and push the CI/CD configuration:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD configuration"
   git push
   ```

5. Go to your GitHub repository, navigate to the "Actions" tab, and ensure the workflow is visible and running on pushes to the main branch.

This completes the initial setup for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now prepared, the new Spring Boot project is initialized within the existing repository, version control is set up, build and dependency management is configured, and a basic CI/CD pipeline is in place. You can now proceed with the actual migration steps as outlined in the migration plan.
