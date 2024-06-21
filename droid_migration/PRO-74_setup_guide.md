
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prepare Development Environment](#1-prepare-development-environment)
2. [Set Up Version Control](#2-set-up-version-control)
3. [Initialize Spring Boot Project](#3-initialize-spring-boot-project)
4. [Configure IDE](#4-configure-ide)
5. [Set Up CI/CD Pipeline](#5-set-up-cicd-pipeline)

## 1. Prepare Development Environment

### 1.1 Install Java 21 JDK
1. Visit the official Oracle website or use a package manager to download Java 21 JDK.
2. Follow the installation instructions for your operating system.
3. Verify the installation by opening a terminal and running:
   ```
   java -version
   ```

### 1.2 Install Spring Boot CLI
1. Visit the official Spring Boot website and download the latest stable version of Spring Boot CLI.
2. Extract the archive and add the `bin` directory to your system's PATH.
3. Verify the installation by running:
   ```
   spring --version
   ```

## 2. Set Up Version Control

### 2.1 Create New Directory for Spring Boot Project
1. Navigate to the root of the existing repository:
   ```
   cd /path/to/jboss-eap-quickstarts
   ```
2. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring-boot
   cd kitchensink-spring-boot
   ```

### 2.2 Initialize Git Repository
1. Initialize a new Git repository within the new directory:
   ```
   git init
   ```
2. Create a `.gitignore` file for Spring Boot:
   ```
   touch .gitignore
   ```
3. Add common Spring Boot exclusions to `.gitignore`:
   ```
   echo "HELP.md
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
   .vscode/" > .gitignore
   ```

## 3. Initialize Spring Boot Project

### 3.1 Use Spring Initializr
1. Visit https://start.spring.io/ in your web browser.
2. Configure the project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Latest stable version (e.g., 2.7.x)
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
   - Validation
   - Lombok
   - Spring Boot DevTools
4. Click "Generate" to download the project ZIP file.

### 3.2 Extract and Move Project Files
1. Extract the downloaded ZIP file.
2. Move the contents to the `kitchensink-spring-boot` directory:
   ```
   mv /path/to/downloaded/kitchensink/* /path/to/jboss-eap-quickstarts/kitchensink-spring-boot/
   ```

## 4. Configure IDE

### 4.1 IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Click "Open" and select the `kitchensink-spring-boot` directory.
3. Wait for IntelliJ to import and index the project.
4. Ensure the project is using Java 21:
   - Go to File > Project Structure > Project
   - Set Project SDK to Java 21
   - Set Project language level to 21
5. Install the "Spring Assistant" plugin for enhanced Spring Boot support:
   - Go to File > Settings > Plugins
   - Search for "Spring Assistant" and install it
   - Restart IntelliJ IDEA

### 4.2 Eclipse
1. Open Eclipse.
2. Go to File > Import > Maven > Existing Maven Projects.
3. Browse to the `kitchensink-spring-boot` directory and select it.
4. Click "Finish" to import the project.
5. Right-click on the project > Properties > Java Compiler
   - Enable project-specific settings
   - Set Compiler compliance level to 21
6. Install Spring Tools 4:
   - Help > Eclipse Marketplace
   - Search for "Spring Tools 4"
   - Install and restart Eclipse

## 5. Set Up CI/CD Pipeline

### 5.1 Create GitHub Actions Workflow
1. In the `kitchensink-spring-boot` directory, create a new directory structure:
   ```
   mkdir -p .github/workflows
   ```
2. Create a new file `ci-cd.yml` in the `.github/workflows` directory:
   ```
   touch .github/workflows/ci-cd.yml
   ```
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

### 5.2 Commit and Push Changes
1. Stage all new files:
   ```
   git add .
   ```
2. Commit the changes:
   ```
   git commit -m "Initial Spring Boot project setup with CI/CD configuration"
   ```
3. Push to the remote repository:
   ```
   git push origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now ready for the actual migration work to begin.
