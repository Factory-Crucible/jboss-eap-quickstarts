
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Build Tool Configuration](#build-tool-configuration)
6. [CI/CD Pipeline Setup](#cicd-pipeline-setup)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- GitHub account (for repository access and CI/CD setup)

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page or use OpenJDK.
2. Download and install Java 21 JDK for your operating system.
3. Set the JAVA_HOME environment variable:
   - On Windows: `setx JAVA_HOME "C:\Program Files\Java\jdk-21"`
   - On macOS/Linux: `export JAVA_HOME=/path/to/jdk-21`
4. Add Java to your PATH:
   - On Windows: `setx PATH "%PATH%;%JAVA_HOME%\bin"`
   - On macOS/Linux: `export PATH=$PATH:$JAVA_HOME/bin`
5. Verify the installation: `java -version`

### 2.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page.
2. Download the latest stable version (3.1.x as of the current date).
3. Extract the archive to a directory of your choice.
4. Add the `bin` directory to your PATH:
   - On Windows: `setx PATH "%PATH%;C:\path\to\spring-3.1.x\bin"`
   - On macOS/Linux: `export PATH=$PATH:/path/to/spring-3.1.x/bin`
5. Verify the installation: `spring --version`

## 3. Project Initialization <a name="project-initialization"></a>

### 3.1 Create New Project Structure

1. Navigate to the root of the existing JBoss EAP project.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-kitchensink
   cd spring-kitchensink
   ```

### 3.2 Initialize Spring Boot Project

1. Use Spring Initializr to create a new project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,validation,lombok,devtools --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink .
   ```
2. This command creates a new Spring Boot project with the specified dependencies in the current directory.

### 3.3 Configure Project Structure

1. Create the following package structure in `src/main/java/com/example/kitchensink`:
   ```
   mkdir -p src/main/java/com/example/kitchensink/{controller,data,model,service,util}
   ```

## 4. Version Control Setup <a name="version-control-setup"></a>

### 4.1 Initialize Git Repository (if not already done)

1. Navigate to the root of the JBoss EAP project.
2. Initialize a Git repository (if not already done):
   ```
   git init
   ```

### 4.2 Create .gitignore File

1. Create a `.gitignore` file in the root directory:
   ```
   touch .gitignore
   ```
2. Add the following content to the `.gitignore` file:
   ```
   # Maven
   target/
   
   # IntelliJ IDEA
   .idea/
   *.iml
   
   # Eclipse
   .classpath
   .project
   .settings/
   
   # Spring Boot
   *.log
   *.gz
   
   # OS-specific
   .DS_Store
   Thumbs.db
   ```

### 4.3 Commit Initial Changes

1. Stage and commit the changes:
   ```
   git add .
   git commit -m "Initial commit for Spring Boot migration"
   ```

## 5. Build Tool Configuration <a name="build-tool-configuration"></a>

### 5.1 Configure Maven (pom.xml)

1. Open `spring-kitchensink/pom.xml` in your preferred text editor.
2. Ensure the Java version is set to 21:
   ```xml
   <properties>
       <java.version>21</java.version>
   </properties>
   ```
3. Add any additional dependencies required for your project.

## 6. CI/CD Pipeline Setup <a name="cicd-pipeline-setup"></a>

### 6.1 Create GitHub Actions Workflow

1. In the root of the JBoss EAP project, create a new directory:
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
         run: |
           cd spring-kitchensink
           mvn clean install
       - name: Run tests
         run: |
           cd spring-kitchensink
           mvn test
   ```

### 6.2 Commit CI/CD Configuration

1. Stage and commit the changes:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD configuration"
   ```

### 6.3 Push Changes to GitHub

1. Add your GitHub repository as a remote (if not already done):
   ```
   git remote add origin https://github.com/your-username/your-repo-name.git
   ```
2. Push the changes to GitHub:
   ```
   git push -u origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now ready, the new Spring Boot project is initialized within the existing repository, version control is set up, and a basic CI/CD pipeline is in place. You can now proceed with the actual migration steps as outlined in the migration plan.
