
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [CI/CD Configuration](#cicd-configuration)

## Prerequisites

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- GitHub account (for CI/CD setup)

## Development Environment Setup

### 1. Install Java 21 JDK

a. Visit the official Oracle JDK download page or use a package manager for your operating system.
b. Download and install Java 21 JDK.
c. Set the JAVA_HOME environment variable:
   - On Windows: Set JAVA_HOME in System Properties > Environment Variables
   - On macOS/Linux: Add `export JAVA_HOME=/path/to/jdk21` to your shell profile file

d. Verify the installation by running:
   ```
   java -version
   ```

### 2. Install Spring Boot CLI

a. Visit the official Spring Boot CLI installation page.
b. Download the latest stable version.
c. Extract the archive and add the `bin` directory to your PATH.
d. Verify the installation by running:
   ```
   spring --version
   ```

### 3. Set up IDE

Choose one of the following IDEs and follow the setup instructions:

#### Option A: IntelliJ IDEA

a. Download and install IntelliJ IDEA (Community or Ultimate edition).
b. Open IntelliJ IDEA and go to Plugins.
c. Search for and install the "Spring Boot" plugin.
d. Restart IntelliJ IDEA.

#### Option B: Eclipse

a. Download and install Eclipse IDE for Enterprise Java and Web Developers.
b. Open Eclipse and go to Help > Eclipse Marketplace.
c. Search for and install "Spring Tools 4".
d. Restart Eclipse.

#### Option C: Visual Studio Code

a. Download and install Visual Studio Code.
b. Open VS Code and go to the Extensions view (Ctrl+Shift+X).
c. Search for and install the following extensions:
   - Spring Boot Tools
   - Java Extension Pack
d. Restart VS Code.

## Project Initialization

1. Open a terminal or command prompt.

2. Navigate to the root directory of your existing JBoss project:
   ```
   cd path/to/jboss-eap-quickstarts
   ```

3. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-kitchensink
   cd spring-kitchensink
   ```

4. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,validation,lombok,devtools,actuator --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink --description="Migrated Kitchensink Application" .
   ```

5. Open the newly created `pom.xml` file and ensure the Java version is set to 21:
   ```xml
   <properties>
       <java.version>21</java.version>
   </properties>
   ```

6. Build the project to ensure everything is set up correctly:
   ```
   ./mvnw clean install
   ```

## Version Control Setup

1. Initialize a new Git repository in the root directory (if not already done):
   ```
   cd ..
   git init
   ```

2. Create a `.gitignore` file in the root directory with the following content:
   ```
   # Ignore Spring Boot specific files
   spring-kitchensink/target/
   spring-kitchensink/.mvn/
   spring-kitchensink/mvnw
   spring-kitchensink/mvnw.cmd

   # Ignore IDE specific files
   .idea/
   *.iml
   .vscode/
   .classpath
   .project
   .settings/

   # Ignore build files
   target/
   build/
   ```

3. Add and commit the changes:
   ```
   git add .
   git commit -m "Initial commit with JBoss and Spring Boot projects"
   ```

4. (Optional) If you're using a remote repository, add it and push the changes:
   ```
   git remote add origin <your-remote-repository-url>
   git push -u origin main
   ```

## CI/CD Configuration

1. Create a `.github/workflows` directory in the root of your project:
   ```
   mkdir -p .github/workflows
   ```

2. Create a new file named `ci-cd.yml` in the `.github/workflows` directory with the following content:
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
         run: |
           cd spring-kitchensink
           mvn clean install

       - name: Run tests
         run: |
           cd spring-kitchensink
           mvn test
   ```

3. Commit and push the changes:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD configuration"
   git push
   ```

4. Go to your GitHub repository, click on the "Actions" tab, and ensure that the workflow is visible and running on new pushes to the main branch.

This completes the setup process for the migration project. You now have a development environment ready with Java 21, Spring Boot CLI, an IDE of your choice, version control set up for both the old and new codebases, and a basic CI/CD pipeline configured with GitHub Actions.
