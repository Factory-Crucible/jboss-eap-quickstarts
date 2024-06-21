
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Spring Boot Project Initialization](#spring-boot-project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)

## Prerequisites

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- Basic familiarity with command-line operations

## Development Environment Setup

### 1. Install Java 21 JDK

a. Visit the official Oracle JDK download page or use an open-source alternative like OpenJDK.
b. Download and install Java 21 JDK for your operating system.
c. Set up JAVA_HOME environment variable:
   - Windows: Set JAVA_HOME in System Properties > Environment Variables
   - macOS/Linux: Add `export JAVA_HOME=/path/to/jdk21` to your shell profile file

d. Verify the installation:
   ```
   java -version
   ```

### 2. Install Spring Boot CLI

a. Visit the Spring Boot CLI installation page.
b. Follow the instructions for your operating system to install Spring Boot CLI.
c. Verify the installation:
   ```
   spring --version
   ```

### 3. Set up IDE with Spring Boot support

We'll use IntelliJ IDEA for this guide, but you can use any IDE with Spring Boot support.

a. Download and install IntelliJ IDEA (Community or Ultimate edition).
b. Open IntelliJ IDEA and go to Plugins.
c. Search for and install the "Spring Boot" plugin.
d. Restart IntelliJ IDEA after installation.

## Spring Boot Project Initialization

### 1. Create a new Spring Boot project

a. Open a terminal or command prompt.
b. Navigate to the root of your existing repository:
   ```
   cd path/to/jboss-eap-quickstarts
   ```
c. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   cd kitchensink-spring
   ```
d. Use Spring Initializr to create a new project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,lombok --groupId=org.jboss.as.quickstarts --artifactId=kitchensink-spring --name=kitchensink-spring --package-name=org.jboss.as.quickstarts.kitchensink --description="Kitchensink Spring Boot Migration" .
   ```

### 2. Import the project into your IDE

a. Open IntelliJ IDEA.
b. Choose "Open" and select the `kitchensink-spring` directory.
c. Wait for the IDE to import and index the project.

### 3. Verify the project structure

Ensure the following structure is present:
```
kitchensink-spring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Version Control Setup

### 1. Update .gitignore

a. Open the `.gitignore` file in the `kitchensink-spring` directory.
b. Add the following entries if not already present:
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

### 2. Initialize Git repository

a. Open a terminal in the `kitchensink-spring` directory.
b. Initialize a new Git repository:
   ```
   git init
   ```
c. Add all files to the repository:
   ```
   git add .
   ```
d. Make the initial commit:
   ```
   git commit -m "Initial Spring Boot project setup"
   ```

## CI/CD Pipeline Configuration

We'll set up a basic GitHub Actions workflow for CI/CD.

### 1. Create GitHub Actions workflow

a. In the `kitchensink-spring` directory, create the following directory structure:
   ```
   mkdir -p .github/workflows
   ```
b. Create a new file named `ci-cd.yml` in the `.github/workflows` directory.
c. Add the following content to `ci-cd.yml`:
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

### 2. Commit and push the CI/CD configuration

a. Stage the new files:
   ```
   git add .github/workflows/ci-cd.yml
   ```
b. Commit the changes:
   ```
   git commit -m "Add GitHub Actions CI/CD workflow"
   ```
c. Push to the remote repository (assuming you've set up a remote repository):
   ```
   git push origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now ready, a new Spring Boot project has been initialized within the existing repository, version control has been set up, and a basic CI/CD pipeline has been configured.

Next steps would involve the actual migration of code, which is outlined in the migration plan. Remember to work within the `kitchensink-spring` directory for all new Spring Boot related development, while keeping the original JBoss project intact in its current location.
