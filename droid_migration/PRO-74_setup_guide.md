
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Development Environment Setup](#development-environment-setup)
4. [Project Initialization](#project-initialization)
5. [Version Control Setup](#version-control-setup)
6. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)

## Introduction

This guide provides step-by-step instructions for setting up the development environment and project structure for migrating the JBoss 'kitchensink' application to Spring Boot with Java 21. Follow these steps to prepare your environment for the migration process.

## Prerequisites

Ensure you have the following tools installed on your system:
- Git
- A GitHub account

## Development Environment Setup

1. Install Java 21 JDK:
   - Visit the official Oracle website or use OpenJDK.
   - Download and install Java 21 JDK for your operating system.
   - Set the JAVA_HOME environment variable to point to the Java 21 installation directory.

2. Install Spring Boot CLI:
   - Visit the official Spring Boot website.
   - Download the latest stable version of Spring Boot CLI.
   - Extract the downloaded archive to a directory of your choice.
   - Add the `bin` directory to your system's PATH.

3. Set up IDE:
   - Download and install your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
   - Install Spring Boot support plugins for your IDE:
     - For IntelliJ IDEA: Install the "Spring Boot Assistant" plugin.
     - For Eclipse: Install the "Spring Tools 4" plugin.
     - For VS Code: Install the "Spring Boot Tools" extension.

## Project Initialization

1. Clone the existing repository:
   ```
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts
   ```

2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-boot-kitchensink
   cd spring-boot-kitchensink
   ```

3. Initialize a new Spring Boot project:
   - Use Spring Initializr (https://start.spring.io/) to generate a new project with the following details:
     - Project: Maven
     - Language: Java
     - Spring Boot: 3.x.x (latest stable version)
     - Group: org.jboss.as.quickstarts
     - Artifact: kitchensink
     - Name: kitchensink
     - Description: Kitchensink quickstart migrated to Spring Boot
     - Package name: org.jboss.as.quickstarts.kitchensink
     - Packaging: Jar
     - Java: 21
   - Add the following dependencies:
     - Spring Web
     - Spring Data JPA
     - H2 Database
     - Spring Boot DevTools
     - Spring Boot Actuator
     - Lombok

4. Download the generated project and extract its contents into the `spring-boot-kitchensink` directory.

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

## CI/CD Pipeline Configuration

1. Create a `.github/workflows` directory in the root of the repository:
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
         run: mvn -B package --file spring-boot-kitchensink/pom.xml
         
       - name: Run tests
         run: mvn test --file spring-boot-kitchensink/pom.xml
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

With these steps completed, your development environment is set up, the Spring Boot project is initialized within the existing repository, version control is configured, and a basic CI/CD pipeline is in place. You can now proceed with the migration process as outlined in the migration plan.
