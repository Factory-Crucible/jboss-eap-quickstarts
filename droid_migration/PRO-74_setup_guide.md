
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

This guide provides step-by-step instructions to set up your development environment and project structure for migrating the JBoss 'kitchensink' application to Spring Boot with Java 21.

## Table of Contents
1. [Development Environment Setup](#1-development-environment-setup)
2. [Project Initialization](#2-project-initialization)
3. [Version Control Setup](#3-version-control-setup)
4. [CI/CD Pipeline Configuration](#4-cicd-pipeline-configuration)

## 1. Development Environment Setup

### 1.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page or use OpenJDK.
2. Download and install Java 21 JDK for your operating system.
3. Set the JAVA_HOME environment variable:
   - Windows: `setx JAVA_HOME "C:\Program Files\Java\jdk-21"`
   - macOS/Linux: `export JAVA_HOME=/path/to/jdk-21`
4. Add Java to your PATH:
   - Windows: `setx PATH "%PATH%;%JAVA_HOME%\bin"`
   - macOS/Linux: `export PATH=$PATH:$JAVA_HOME/bin`

### 1.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page.
2. Download the latest stable version.
3. Extract the archive and add the `bin` directory to your PATH.
4. Verify installation: `spring --version`

### 1.3 Set up IDE with Spring Boot support

1. Download and install your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
2. Install Spring Boot support plugins:
   - IntelliJ IDEA: Spring Boot plugin is built-in.
   - Eclipse: Install Spring Tools 4 from the Eclipse Marketplace.
   - VS Code: Install the Spring Boot Tools extension.

## 2. Project Initialization

### 2.1 Create a new top-level directory

1. Navigate to the root of your existing project.
2. Create a new directory: `mkdir spring-boot-kitchensink`
3. Move into the new directory: `cd spring-boot-kitchensink`

### 2.2 Use Spring Initializr

1. Open a web browser and go to https://start.spring.io/
2. Configure the project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Latest stable version
   - Project Metadata:
     - Group: com.example
     - Artifact: kitchensink
     - Name: kitchensink
     - Description: Kitchensink application migrated to Spring Boot
     - Package name: com.example.kitchensink
   - Packaging: Jar
   - Java: 21
3. Add dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Validation
   - Lombok
   - Spring Boot DevTools
4. Click "Generate" to download the project ZIP file.

### 2.3 Extract and set up the project

1. Extract the downloaded ZIP file into the `spring-boot-kitchensink` directory.
2. Open the project in your IDE.

## 3. Version Control Setup

### 3.1 Initialize Git repository

1. Open a terminal in the `spring-boot-kitchensink` directory.
2. Initialize a new Git repository: `git init`

### 3.2 Create .gitignore file

1. Create a `.gitignore` file in the project root:
   ```
   touch .gitignore
   ```
2. Add the following content to `.gitignore`:
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

### 3.3 Make initial commit

1. Stage all files: `git add .`
2. Commit the files: `git commit -m "Initial Spring Boot project setup"`

## 4. CI/CD Pipeline Configuration

### 4.1 Set up GitHub Actions

1. Create a `.github/workflows` directory in your project root:
   ```
   mkdir -p .github/workflows
   ```
2. Create a new file `ci-cd.yml` in the `.github/workflows` directory:
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
         run: mvn clean install
       - name: Run tests
         run: mvn test
   ```

### 4.2 Commit CI/CD configuration

1. Stage the new file: `git add .github/workflows/ci-cd.yml`
2. Commit the file: `git commit -m "Add CI/CD configuration"`

With these steps completed, your development environment and project structure are now set up for the migration process. You can proceed with the actual migration steps as outlined in the migration plan.
