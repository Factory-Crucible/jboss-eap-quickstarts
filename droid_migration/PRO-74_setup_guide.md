
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
1. Download Java 21 JDK from the official Oracle website or use an OpenJDK distribution.
2. Install the JDK following the instructions for your operating system.
3. Set the JAVA_HOME environment variable to point to your Java 21 installation.
4. Add the Java bin directory to your system PATH.

### 1.2 Install Spring Boot CLI
1. Download the latest Spring Boot CLI from the official Spring website.
2. Extract the archive to a directory of your choice.
3. Add the bin directory of the extracted archive to your system PATH.

### 1.3 Set up IDE
1. Install your preferred IDE (e.g., IntelliJ IDEA, Eclipse, or Visual Studio Code).
2. Install the following plugins for your IDE:
   - Spring Boot plugin
   - Java 21 support plugin
   - Lombok plugin

## 2. Project Structure Setup

1. Open a terminal and navigate to the root of the jboss-eap-quickstarts repository.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-boot-kitchensink
   ```
3. Move the existing kitchensink project into a subdirectory:
   ```
   mkdir jboss-kitchensink
   mv * jboss-kitchensink/
   mv .* jboss-kitchensink/
   ```
   Note: This command may produce warnings for . and .. directories, which can be ignored.

## 3. Spring Boot Project Initialization

1. Navigate to the spring-boot-kitchensink directory:
   ```
   cd spring-boot-kitchensink
   ```
2. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,lombok --groupId=org.jboss.as.quickstarts --artifactId=spring-boot-kitchensink --name="Spring Boot Kitchensink" --package-name=org.jboss.as.quickstarts.kitchensink --description="Spring Boot version of JBoss Kitchensink application" .
   ```
3. Open the generated pom.xml file and add the following dependencies:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
   </dependency>
   ```

## 4. Version Control Setup

1. Initialize a new Git repository in the spring-boot-kitchensink directory:
   ```
   git init
   ```
2. Create a .gitignore file:
   ```
   touch .gitignore
   ```
3. Add the following content to the .gitignore file:
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
4. Stage and commit the initial project files:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 5. CI/CD Pipeline Configuration

1. Create a .github/workflows directory in the spring-boot-kitchensink folder:
   ```
   mkdir -p .github/workflows
   ```
2. Create a new file named ci-cd.yml in the .github/workflows directory:
   ```
   touch .github/workflows/ci-cd.yml
   ```
3. Add the following content to the ci-cd.yml file:
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
         run: mvn clean install
       - name: Run tests
         run: mvn test
   ```
4. Commit the CI/CD configuration:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD pipeline configuration"
   ```

With these steps completed, you have set up the development environment, initialized the Spring Boot project, configured version control, and set up a basic CI/CD pipeline. You are now ready to begin the migration process as outlined in the migration plan.
