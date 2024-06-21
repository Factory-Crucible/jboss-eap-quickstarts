
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:
- Admin access to your development machine
- GitHub account with access to the target repository
- Basic familiarity with command-line operations

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Download the appropriate JDK 21 installer for your operating system
3. Run the installer and follow the installation wizard
4. Verify the installation by opening a terminal and running:
   ```
   java -version
   ```
   You should see output indicating Java 21

### 2.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page: https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli
2. Follow the installation instructions for your operating system
3. Verify the installation by opening a terminal and running:
   ```
   spring --version
   ```
   You should see output indicating the Spring Boot CLI version

### 2.3 Set up IDE

We recommend using IntelliJ IDEA for this project, but you can use any IDE with good Spring Boot support.

For IntelliJ IDEA:
1. Download and install IntelliJ IDEA Ultimate Edition from: https://www.jetbrains.com/idea/download/
2. During installation, ensure you select the option to include the "Spring Boot" plugin
3. After installation, open IntelliJ IDEA and go to File > Settings > Plugins
4. Search for "Spring Boot" and ensure it's installed and enabled

## 3. Project Initialization <a name="project-initialization"></a>

### 3.1 Clone the Existing Repository

1. Open a terminal and navigate to your desired workspace directory
2. Clone the existing repository:
   ```
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts
   ```

### 3.2 Create a New Directory for the Spring Boot Project

1. Inside the `jboss-eap-quickstarts` directory, create a new folder for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   cd kitchensink-spring
   ```

### 3.3 Initialize Spring Boot Project

1. Visit https://start.spring.io/ in your web browser
2. Configure the project as follows:
   - Project: Maven
   - Language: Java
   - Spring Boot: 3.1.x (or the latest stable version)
   - Project Metadata:
     - Group: org.jboss.as.quickstarts
     - Artifact: kitchensink-spring
     - Name: kitchensink-spring
     - Description: Kitchensink quickstart migrated to Spring Boot
     - Package name: org.jboss.as.quickstarts.kitchensink
     - Packaging: Jar
     - Java: 21
   - Dependencies: Add the following
     - Spring Web
     - Spring Data JPA
     - H2 Database
     - Spring Boot DevTools
     - Spring Boot Actuator
     - Spring Security
3. Click "Generate" to download the project zip file
4. Extract the contents of the zip file into the `kitchensink-spring` directory you created earlier

## 4. Version Control Setup <a name="version-control-setup"></a>

### 4.1 Configure Git

1. In the `kitchensink-spring` directory, initialize a new Git repository:
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

4. Stage and commit the initial Spring Boot project:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 5. CI/CD Pipeline Configuration <a name="cicd-pipeline-configuration"></a>

### 5.1 Set up GitHub Actions

1. In the `kitchensink-spring` directory, create a new directory for GitHub Actions workflows:
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

4. Commit the GitHub Actions workflow:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add GitHub Actions CI/CD workflow"
   ```

5. Push the changes to the remote repository:
   ```
   git push origin main
   ```

With these steps completed, you have set up the development environment, initialized the Spring Boot project within the existing repository, configured version control, and set up a basic CI/CD pipeline using GitHub Actions. You are now ready to begin the migration process as outlined in the migration plan.
