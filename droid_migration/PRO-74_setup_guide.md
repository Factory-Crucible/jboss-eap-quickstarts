
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

This guide provides step-by-step instructions to set up your development environment and project structure for migrating the JBoss 'kitchensink' application to Spring Boot with Java 21.

## Table of Contents
1. [Development Environment Setup](#1-development-environment-setup)
2. [Project Structure Setup](#2-project-structure-setup)
3. [Spring Boot Project Initialization](#3-spring-boot-project-initialization)
4. [Version Control Setup](#4-version-control-setup)
5. [CI/CD Pipeline Configuration](#5-cicd-pipeline-configuration)

## 1. Development Environment Setup

### 1.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page or use an OpenJDK distribution.
2. Download and install Java 21 JDK for your operating system.
3. Set the JAVA_HOME environment variable:
   - On Windows: `setx JAVA_HOME "C:\Program Files\Java\jdk-21"`
   - On macOS/Linux: `export JAVA_HOME=/path/to/jdk-21`
4. Add Java to your PATH:
   - On Windows: `setx PATH "%PATH%;%JAVA_HOME%\bin"`
   - On macOS/Linux: `export PATH=$PATH:$JAVA_HOME/bin`
5. Verify the installation by running `java -version` in your terminal.

### 1.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page.
2. Download the latest stable version of Spring Boot CLI.
3. Extract the archive and add the `bin` directory to your PATH:
   - On Windows: `setx PATH "%PATH%;C:\path\to\spring-boot-cli\bin"`
   - On macOS/Linux: `export PATH=$PATH:/path/to/spring-boot-cli/bin`
4. Verify the installation by running `spring --version` in your terminal.

## 2. Project Structure Setup

1. Navigate to the root of the jboss-eap-quickstarts repository.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-boot-kitchensink
   ```
3. Move the existing kitchensink project into a subdirectory:
   ```
   mkdir jboss-kitchensink
   mv * jboss-kitchensink/
   ```
   (Note: You may need to move hidden files separately)
4. Your project structure should now look like this:
   ```
   jboss-eap-quickstarts/
   ├── jboss-kitchensink/
   │   └── (original kitchensink files)
   └── spring-boot-kitchensink/
       └── (empty for now)
   ```

## 3. Spring Boot Project Initialization

1. Navigate to the `spring-boot-kitchensink` directory:
   ```
   cd spring-boot-kitchensink
   ```
2. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,validation --groupId=org.jboss.as.quickstarts --artifactId=kitchensink --name=kitchensink --package-name=org.jboss.as.quickstarts.kitchensink --description="Spring Boot version of kitchensink quickstart" .
   ```
3. Review the generated `pom.xml` file and ensure all necessary dependencies are included.

## 4. Version Control Setup

1. Initialize a new Git repository in the `spring-boot-kitchensink` directory:
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
4. Make the initial commit:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 5. CI/CD Pipeline Configuration

1. Create a `.github/workflows` directory in the root of the repository:
   ```
   mkdir -p .github/workflows
   ```
2. Create a YAML file for GitHub Actions:
   ```
   touch .github/workflows/spring-boot-kitchensink-ci.yml
   ```
3. Add the following content to the YAML file:
   ```yaml
   name: Spring Boot Kitchensink CI

   on:
     push:
       paths:
         - 'spring-boot-kitchensink/**'
     pull_request:
       paths:
         - 'spring-boot-kitchensink/**'

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
       - name: Run tests
         run: |
           cd spring-boot-kitchensink
           ./mvnw test
   ```
4. Commit the CI/CD configuration:
   ```
   git add .github/workflows/spring-boot-kitchensink-ci.yml
   git commit -m "Add GitHub Actions CI workflow for Spring Boot Kitchensink"
   ```

With these steps completed, your development environment and project structure are now set up for the migration process. You can proceed with the actual migration steps as outlined in the migration plan.
