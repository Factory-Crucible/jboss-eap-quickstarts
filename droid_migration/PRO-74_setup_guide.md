
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Development Environment Setup](#1-development-environment-setup)
2. [Project Initialization](#2-project-initialization)
3. [Version Control Setup](#3-version-control-setup)
4. [CI/CD Pipeline Configuration](#4-cicd-pipeline-configuration)

## 1. Development Environment Setup

### 1.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Download the appropriate JDK 21 installer for your operating system.
3. Run the installer and follow the installation wizard.
4. Set the JAVA_HOME environment variable:
   - Windows: 
     ```
     setx JAVA_HOME "C:\Program Files\Java\jdk-21"
     ```
   - macOS/Linux:
     ```
     echo 'export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home' >> ~/.bash_profile
     source ~/.bash_profile
     ```
5. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 1.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI download page: https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli
2. Download the latest stable version of Spring Boot CLI.
3. Extract the archive to a directory of your choice.
4. Add the `bin` directory to your PATH:
   - Windows:
     ```
     setx PATH "%PATH%;C:\path\to\spring-boot-cli\bin"
     ```
   - macOS/Linux:
     ```
     echo 'export PATH=$PATH:/path/to/spring-boot-cli/bin' >> ~/.bash_profile
     source ~/.bash_profile
     ```
5. Verify the installation by opening a new terminal and running:
   ```
   spring --version
   ```

### 1.3 Set Up IDE with Spring Boot Support

For this guide, we'll use IntelliJ IDEA as an example:

1. Download and install IntelliJ IDEA from: https://www.jetbrains.com/idea/download/
2. Launch IntelliJ IDEA.
3. Go to File > Settings (on Windows/Linux) or IntelliJ IDEA > Preferences (on macOS).
4. Navigate to Plugins.
5. Search for "Spring Boot" and install the "Spring Boot Assistant" plugin.
6. Restart IntelliJ IDEA when prompted.

## 2. Project Initialization

### 2.1 Create New Spring Boot Project

1. Open a web browser and go to https://start.spring.io/
2. Configure the project as follows:
   - Project: Maven
   - Language: Java
   - Spring Boot: 3.1.x (latest stable version)
   - Project Metadata:
     - Group: org.jboss.as.quickstarts
     - Artifact: kitchensink-spring
     - Name: kitchensink-spring
     - Description: Kitchensink quickstart migrated to Spring Boot
     - Package name: org.jboss.as.quickstarts.kitchensink
     - Packaging: Jar
     - Java: 21
3. Add the following dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Boot DevTools
   - Spring Boot Actuator
   - Lombok
4. Click "Generate" to download the project ZIP file.

### 2.2 Integrate New Project into Existing Repository

1. Extract the downloaded ZIP file.
2. Open a terminal and navigate to your existing repository root.
3. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   ```
4. Copy the contents of the extracted Spring Boot project into the new directory:
   ```
   cp -R /path/to/extracted/project/* kitchensink-spring/
   ```
5. Move the existing JBoss project into a subdirectory:
   ```
   mkdir kitchensink-jboss
   git mv * kitchensink-jboss/
   git mv kitchensink-spring/* .
   rmdir kitchensink-spring
   ```
6. Update the `.gitignore` file in the root directory to include Spring Boot specific entries:
   ```
   echo '
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
   ' >> .gitignore
   ```

## 3. Version Control Setup

### 3.1 Update Git Repository

1. Stage the new files:
   ```
   git add .
   ```
2. Commit the changes:
   ```
   git commit -m "Set up Spring Boot project structure for migration"
   ```
3. Push the changes to the remote repository:
   ```
   git push origin main
   ```

## 4. CI/CD Pipeline Configuration

### 4.1 Set Up GitHub Actions

1. In your repository, create a new directory:
   ```
   mkdir -p .github/workflows
   ```
2. Create a new file named `ci-cd.yml` in the `.github/workflows` directory:
   ```
   touch .github/workflows/ci-cd.yml
   ```
3. Open the `ci-cd.yml` file in your text editor and add the following content:
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
       - uses: actions/checkout@v3
       - name: Set up JDK 21
         uses: actions/setup-java@v3
         with:
           java-version: '21'
           distribution: 'oracle'
       - name: Build with Maven
         run: mvn clean install
       - name: Run tests
         run: mvn test
   ```
4. Commit and push the new workflow file:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add GitHub Actions CI/CD workflow"
   git push origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now prepared, the new Spring Boot project is initialized within the existing repository, version control is updated, and a basic CI/CD pipeline is configured. You can now proceed with the actual migration steps as outlined in the migration plan.
