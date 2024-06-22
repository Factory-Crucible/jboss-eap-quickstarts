
# Setup Guide for Migrating JBoss 'kitchensink' to Spring Boot

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
- GitHub account (for repository management and CI/CD setup)

## Development Environment Setup

### 1. Install Java 21 JDK

a. Visit the official Oracle JDK download page or use an open-source alternative like AdoptOpenJDK.
b. Download and install Java 21 JDK for your operating system.
c. Set the JAVA_HOME environment variable:
   - On Windows: Set JAVA_HOME in System Properties > Environment Variables
   - On macOS/Linux: Add `export JAVA_HOME=/path/to/jdk21` to your ~/.bash_profile or ~/.zshrc

d. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 2. Install Spring Boot CLI

a. Visit the Spring Boot CLI installation page.
b. Download the latest stable version.
c. Extract the archive and add the `bin` directory to your PATH.
d. Verify the installation:
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

1. Open your chosen IDE.

2. Create a new directory in the root of the jboss-eap-quickstarts repository:
   ```
   mkdir -p jboss-eap-quickstarts/kitchensink-spring-boot
   ```

3. Navigate to the new directory:
   ```
   cd jboss-eap-quickstarts/kitchensink-spring-boot
   ```

4. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,lombok --groupId=org.jboss.as.quickstarts --artifactId=kitchensink-spring-boot --name=kitchensink-spring-boot --package-name=org.jboss.as.quickstarts.kitchensink --description="Kitchensink quickstart migrated to Spring Boot" .
   ```

5. Open the newly created project in your IDE.

6. Update the `pom.xml` file to include the latest stable version of Spring Boot:
   ```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>3.1.0</version> <!-- Use the latest stable version -->
       <relativePath/>
   </parent>
   ```

7. Refresh the project in your IDE to download the new dependencies.

## Version Control Setup

1. Open a terminal and navigate to the root of the jboss-eap-quickstarts repository.

2. Ensure you're on the main branch and have the latest changes:
   ```
   git checkout main
   git pull origin main
   ```

3. Create a new branch for the Spring Boot migration:
   ```
   git checkout -b spring-boot-migration
   ```

4. Add the new Spring Boot project files to Git:
   ```
   git add kitchensink-spring-boot
   ```

5. Commit the initial Spring Boot project setup:
   ```
   git commit -m "Initial setup for Spring Boot migration of kitchensink quickstart"
   ```

6. Push the new branch to the remote repository:
   ```
   git push -u origin spring-boot-migration
   ```

## CI/CD Configuration

1. In the root of the jboss-eap-quickstarts repository, create a new directory for GitHub Actions workflows:
   ```
   mkdir -p .github/workflows
   ```

2. Create a new file named `spring-boot-ci.yml` in the `.github/workflows` directory:
   ```
   touch .github/workflows/spring-boot-ci.yml
   ```

3. Open `spring-boot-ci.yml` in your text editor and add the following content:

   ```yaml
   name: Spring Boot CI

   on:
     push:
       branches: [ spring-boot-migration ]
     pull_request:
       branches: [ spring-boot-migration ]

   jobs:
     build:
       runs-on: ubuntu-latest

       steps:
       - uses: actions/checkout@v3
       - name: Set up JDK 21
         uses: actions/setup-java@v3
         with:
           java-version: '21'
           distribution: 'adopt'
       - name: Build with Maven
         run: |
           cd kitchensink-spring-boot
           mvn clean install
       - name: Run tests
         run: |
           cd kitchensink-spring-boot
           mvn test
   ```

4. Commit the CI configuration:
   ```
   git add .github/workflows/spring-boot-ci.yml
   git commit -m "Add GitHub Actions CI workflow for Spring Boot project"
   git push
   ```

5. Go to your GitHub repository, click on the "Actions" tab, and verify that the new workflow is listed and running.

This completes the initial setup for migrating the JBoss 'kitchensink' quickstart to Spring Boot. The development environment is now prepared, the new Spring Boot project is initialized within the existing repository structure, version control is set up, and a basic CI pipeline is configured. You can now proceed with the actual migration steps as outlined in the migration plan.
