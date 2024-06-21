
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

This guide will walk you through the process of setting up your development environment and project structure for migrating the JBoss 'kitchensink' application to Spring Boot with Java 21.

## Table of Contents
1. [Install Java 21 JDK](#1-install-java-21-jdk)
2. [Install Spring Boot CLI](#2-install-spring-boot-cli)
3. [Set Up IDE](#3-set-up-ide)
4. [Initialize Spring Boot Project](#4-initialize-spring-boot-project)
5. [Set Up Version Control](#5-set-up-version-control)
6. [Configure CI/CD Pipeline](#6-configure-cicd-pipeline)

## 1. Install Java 21 JDK

1. Visit the official Oracle JDK download page: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Download the appropriate JDK 21 installer for your operating system.
3. Run the installer and follow the installation wizard.
4. Set the JAVA_HOME environment variable:
   - Windows: 
     1. Right-click on 'This PC' > Properties > Advanced system settings > Environment Variables
     2. Add a new system variable JAVA_HOME with the path to your JDK installation (e.g., C:\Program Files\Java\jdk-21)
   - macOS/Linux:
     1. Add the following line to your ~/.bash_profile or ~/.zshrc:
        ```
        export JAVA_HOME=/path/to/your/jdk-21
        ```
5. Add Java to your PATH:
   - Windows: Add %JAVA_HOME%\bin to your Path environment variable
   - macOS/Linux: Add the following line to your ~/.bash_profile or ~/.zshrc:
     ```
     export PATH=$JAVA_HOME/bin:$PATH
     ```
6. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

## 2. Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page: https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli
2. Follow the installation instructions for your operating system:
   - Windows: Use SDKMAN! or download the zip file and add it to your PATH
   - macOS: Use Homebrew: `brew install spring-boot`
   - Linux: Use SDKMAN! or your package manager
3. Verify the installation by opening a new terminal and running:
   ```
   spring --version
   ```

## 3. Set Up IDE

We recommend using IntelliJ IDEA or Eclipse with Spring Tools Suite (STS) for this project.

### IntelliJ IDEA:
1. Download and install IntelliJ IDEA Ultimate Edition: https://www.jetbrains.com/idea/download/
2. During setup, ensure you select the "Spring Boot" plugin.

### Eclipse with Spring Tools Suite:
1. Download Eclipse IDE: https://www.eclipse.org/downloads/
2. Install Spring Tools Suite:
   - In Eclipse, go to Help > Eclipse Marketplace
   - Search for "Spring Tools"
   - Install "Spring Tools 4"

## 4. Initialize Spring Boot Project

1. Open your web browser and go to https://start.spring.io/
2. Configure your project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Select the latest stable version
   - Project Metadata:
     - Group: com.example
     - Artifact: kitchensink
     - Name: kitchensink
     - Description: Migrated kitchensink application
     - Package name: com.example.kitchensink
     - Packaging: Jar
     - Java: 21
   - Dependencies: Add the following
     - Spring Web
     - Spring Data JPA
     - H2 Database
     - Validation
     - Lombok
     - Spring Boot DevTools
3. Click "Generate" to download the project zip file.
4. Extract the zip file to your desired project location.
5. Open the project in your IDE.

## 5. Set Up Version Control

1. Open a terminal and navigate to your project directory.
2. Initialize a new Git repository:
   ```
   git init
   ```
3. Create a `.gitignore` file in the project root:
   ```
   touch .gitignore
   ```
4. Add the following content to `.gitignore`:
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
5. Stage and commit your initial project files:
   ```
   git add .
   git commit -m "Initial commit: Spring Boot project setup"
   ```

## 6. Configure CI/CD Pipeline

1. Create a `.github/workflows` directory in your project root:
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
         run: mvn clean install
       - name: Run tests
         run: mvn test
   ```
4. Commit the CI/CD configuration:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD configuration"
   ```
5. Create a new repository on GitHub:
   - Go to https://github.com/new
   - Name your repository (e.g., "kitchensink-spring-boot")
   - Keep it private if desired
   - Do not initialize with README, .gitignore, or license
6. Push your local repository to GitHub:
   ```
   git remote add origin https://github.com/your-username/kitchensink-spring-boot.git
   git branch -M main
   git push -u origin main
   ```

Your development environment and initial project structure are now set up and ready for the migration process. The CI/CD pipeline will automatically run on every push to the main branch and on pull requests.
