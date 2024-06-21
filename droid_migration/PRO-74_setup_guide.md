
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Structure Setup](#project-structure-setup)
4. [Spring Boot Project Initialization](#spring-boot-project-initialization)
5. [Version Control Setup](#version-control-setup)
6. [CI/CD Configuration](#cicd-configuration)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the migration process, ensure you have the following:
- A computer with at least 8GB RAM and 20GB free disk space
- Administrator access to install software
- Internet connection for downloading tools and dependencies

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Download the appropriate JDK 21 installer for your operating system
3. Run the installer and follow the installation wizard
4. Set the JAVA_HOME environment variable:
   - Windows: 
     1. Right-click on 'This PC' > Properties > Advanced system settings > Environment Variables
     2. Add a new system variable JAVA_HOME with the path to your JDK installation (e.g., C:\Program Files\Java\jdk-21)
   - macOS/Linux:
     1. Add the following to your ~/.bash_profile or ~/.zshrc:
        ```
        export JAVA_HOME=/path/to/jdk-21
        export PATH=$JAVA_HOME/bin:$PATH
        ```
5. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 2.2 Install Spring Boot CLI

1. Visit the official Spring Boot CLI download page: https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli
2. Download the latest stable version of Spring Boot CLI
3. Extract the downloaded archive to a directory of your choice
4. Add the bin directory to your PATH:
   - Windows:
     1. Edit the system PATH variable and add the path to the bin directory
   - macOS/Linux:
     1. Add the following to your ~/.bash_profile or ~/.zshrc:
        ```
        export PATH=/path/to/spring-boot-cli/bin:$PATH
        ```
5. Verify the installation by opening a new terminal and running:
   ```
   spring --version
   ```

## 3. Project Structure Setup <a name="project-structure-setup"></a>

1. Navigate to the root of your existing JBoss 'kitchensink' project
2. Create a new directory for the Spring Boot migration:
   ```
   mkdir spring-boot-migration
   ```
3. Move the existing JBoss project into a subdirectory:
   ```
   mkdir jboss-original
   mv * jboss-original/
   mv .* jboss-original/
   ```
   (Note: You may need to move hidden files separately)

## 4. Spring Boot Project Initialization <a name="spring-boot-project-initialization"></a>

1. Navigate to the `spring-boot-migration` directory:
   ```
   cd spring-boot-migration
   ```
2. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,actuator,validation --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink kitchensink
   ```
3. Move the generated files to the current directory:
   ```
   mv kitchensink/* .
   mv kitchensink/.* .
   rmdir kitchensink
   ```

## 5. Version Control Setup <a name="version-control-setup"></a>

1. Initialize a new Git repository in the project root:
   ```
   git init
   ```
2. Create a `.gitignore` file:
   ```
   touch .gitignore
   ```
3. Add the following content to `.gitignore`:
   ```
   # Spring Boot
   target/
   !.mvn/wrapper/maven-wrapper.jar
   !**/src/main/**/target/
   !**/src/test/**/target/

   # IDEs
   .idea/
   *.iws
   *.iml
   *.ipr
   .vscode/
   *.swp
   *~.nib
   local.properties

   # Compiled files
   *.class
   *.jar
   *.war
   *.ear

   # Logs
   *.log
   log/
   logs/

   # OS-specific
   .DS_Store
   Thumbs.db
   ```
4. Add and commit the initial project structure:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 6. CI/CD Configuration <a name="cicd-configuration"></a>

1. Create a `.github/workflows` directory in your project root:
   ```
   mkdir -p .github/workflows
   ```
2. Create a YAML file for GitHub Actions:
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

With these steps completed, you have set up the development environment, initialized the Spring Boot project, configured version control, and set up a basic CI/CD pipeline. You are now ready to begin the actual migration process as outlined in the migration plan.
