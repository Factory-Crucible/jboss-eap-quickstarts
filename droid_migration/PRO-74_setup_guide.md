
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

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
5. Verify installation: `java -version`

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

### 2.1 Create a new top-level directory for the Spring Boot project
1. Navigate to the root of the existing JBoss project.
2. Create a new directory: `mkdir spring-boot-kitchensink`
3. Move into the new directory: `cd spring-boot-kitchensink`

### 2.2 Initialize Spring Boot Project
1. Visit [Spring Initializr](https://start.spring.io/).
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
5. Extract the contents of the ZIP file into the `spring-boot-kitchensink` directory.

## 3. Version Control Setup

### 3.1 Update .gitignore
1. Open the `.gitignore` file in the `spring-boot-kitchensink` directory.
2. Add the following entries if not already present:
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

### 3.2 Initialize Git repository
1. Navigate to the `spring-boot-kitchensink` directory.
2. Initialize a new Git repository: `git init`
3. Add all files to the repository: `git add .`
4. Make the initial commit: `git commit -m "Initial commit: Spring Boot project setup"`

## 4. CI/CD Pipeline Configuration

### 4.1 Set up GitHub Actions
1. In the `spring-boot-kitchensink` directory, create a new directory structure: `mkdir -p .github/workflows`
2. Create a new file: `touch .github/workflows/ci-cd.yml`
3. Open `ci-cd.yml` and add the following content:
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

### 4.2 Configure build jobs
1. Add the following job to the `ci-cd.yml` file:
   ```yaml
   package:
     needs: build
     runs-on: ubuntu-latest
     steps:
     - uses: actions/checkout@v2
     - name: Set up JDK 21
       uses: actions/setup-java@v2
       with:
         java-version: '21'
         distribution: 'adopt'
     - name: Package application
       run: mvn package -DskipTests
     - name: Upload artifact
       uses: actions/upload-artifact@v2
       with:
         name: kitchensink
         path: target/*.jar
   ```

### 4.3 Set up staging and production environments
1. For staging and production environments, you'll need to set up separate deployment jobs. Add placeholders for now:
   ```yaml
   deploy-staging:
     needs: package
     runs-on: ubuntu-latest
     steps:
     - run: echo "Deploy to staging environment"

   deploy-production:
     needs: deploy-staging
     runs-on: ubuntu-latest
     steps:
     - run: echo "Deploy to production environment"
   ```

2. Commit the changes to the repository:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD pipeline configuration"
   ```

This completes the setup guide for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now prepared, the new Spring Boot project is initialized, version control is set up, and a basic CI/CD pipeline is configured. You can now proceed with the actual migration steps as outlined in the migration plan.
