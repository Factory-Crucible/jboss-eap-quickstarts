
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Development Environment Setup](#1-development-environment-setup)
2. [Project Initialization](#2-project-initialization)
3. [Version Control Setup](#3-version-control-setup)
4. [Build and Dependency Management](#4-build-and-dependency-management)
5. [Logging Configuration](#5-logging-configuration)
6. [Containerization Tools](#6-containerization-tools)
7. [CI/CD Tools](#7-cicd-tools)
8. [MongoDB Setup (Optional)](#8-mongodb-setup-optional)

## 1. Development Environment Setup

### 1.1 Install Java 21 JDK
1. Visit the official Oracle JDK download page or use OpenJDK.
2. Download and install Java 21 JDK for your operating system.
3. Set the JAVA_HOME environment variable to the JDK installation directory.
4. Add the JDK's bin directory to your system's PATH.

### 1.2 Install Spring Boot CLI
1. Visit the Spring Boot CLI installation page.
2. Follow the instructions to install Spring Boot CLI for your operating system.
3. Verify the installation by running `spring --version` in your terminal.

### 1.3 Set up IDE with Spring Boot support
1. Download and install your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
2. Install the Spring Boot plugin or extension for your IDE.
   - For IntelliJ IDEA: Install the "Spring Assistant" plugin.
   - For Eclipse: Install the "Spring Tools 4" plugin.
   - For VS Code: Install the "Spring Boot Tools" extension.

## 2. Project Initialization

### 2.1 Use Spring Initializr
1. Go to https://start.spring.io/
2. Configure the project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Latest stable version
   - Project Metadata:
     - Group: com.example
     - Artifact: kitchensink
     - Name: kitchensink
     - Description: Migrated Kitchensink application
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

### 2.2 Set up project structure
1. Extract the downloaded ZIP file.
2. In the root of the existing JBoss project, create a new directory called `spring-kitchensink`.
3. Move the contents of the extracted ZIP file into the `spring-kitchensink` directory.
4. Rename the existing JBoss project directory to `jboss-kitchensink`.

## 3. Version Control Setup

### 3.1 Initialize Git repository
1. Open a terminal and navigate to the root of the project.
2. Run `git init` to initialize a new Git repository.

### 3.2 Create .gitignore file
1. In the root directory, create a file named `.gitignore`.
2. Add the following content to the file:

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
1. Run `git add .` to stage all files.
2. Run `git commit -m "Initial commit: Spring Boot project setup"` to commit the changes.

## 4. Build and Dependency Management

### 4.1 Update pom.xml
1. Open `spring-kitchensink/pom.xml` in your IDE.
2. Review the dependencies and ensure all required dependencies are present.
3. Add any additional dependencies specific to your project needs.

### 4.2 Set up Maven wrapper
1. Open a terminal and navigate to the `spring-kitchensink` directory.
2. Run `mvn wrapper:wrapper` to generate the Maven wrapper files.

## 5. Logging Configuration

### 5.1 Configure SLF4J with Logback
1. Open `spring-kitchensink/src/main/resources/application.properties`.
2. Add the following logging configuration:

```properties
logging.level.root=INFO
logging.level.com.example.kitchensink=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## 6. Containerization Tools

### 6.1 Install Docker
1. Visit the official Docker website: https://www.docker.com/get-started
2. Download and install Docker Desktop for your operating system.
3. Follow the installation instructions provided by Docker.
4. Verify the installation by running `docker --version` in your terminal.

## 7. CI/CD Tools

### 7.1 Set up GitHub Actions
1. In the root of your project, create a directory named `.github/workflows`.
2. Inside this directory, create a file named `ci-cd.yml`.
3. Add a basic GitHub Actions workflow to this file:

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
    - uses: actions/checkout@v2
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'adopt'
    - name: Build with Maven
      run: mvn -B package --file spring-kitchensink/pom.xml
```

## 8. MongoDB Setup (Optional)

### 8.1 Install MongoDB locally
1. Visit the official MongoDB download page: https://www.mongodb.com/try/download/community
2. Download and install MongoDB Community Server for your operating system.
3. Follow the installation instructions provided by MongoDB.
4. Verify the installation by running `mongod --version` in your terminal.

### 8.2 Set up MongoDB Atlas (for cloud deployment)
1. Go to MongoDB Atlas: https://www.mongodb.com/cloud/atlas
2. Sign up for a free account or log in if you already have one.
3. Create a new cluster by following the Atlas UI instructions.
4. Once the cluster is created, click on "Connect" and follow the instructions to get your connection string.

This completes the setup guide for the JBoss 'kitchensink' to Spring Boot migration project. You now have a development environment ready for the migration process. Remember to refer to the migration plan for the actual steps of migrating the application code and functionality.
