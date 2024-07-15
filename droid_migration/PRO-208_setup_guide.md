
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Structure Setup](#project-structure-setup)
4. [Spring Boot Project Initialization](#spring-boot-project-initialization)
5. [Version Control Setup](#version-control-setup)
6. [Continuous Integration Setup](#continuous-integration-setup)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:

- Access to the existing JBoss EAP Quickstarts repository
- Administrator privileges on your development machine
- Internet connection for downloading necessary tools and dependencies

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle website or use a package manager to download Java 21 JDK.
2. Follow the installation instructions for your operating system.
3. Verify the installation by opening a terminal and running:
   ```
   java -version
   ```

### 2.2 Install Spring Boot CLI

1. Visit the official Spring Boot website and download the latest stable Spring Boot CLI.
2. Extract the archive and add the `bin` directory to your system's PATH.
3. Verify the installation by running:
   ```
   spring --version
   ```

### 2.3 Set up IDE

1. Download and install your preferred IDE (e.g., IntelliJ IDEA or Eclipse).
2. Install the Spring Boot plugin for your IDE:
   - For IntelliJ IDEA: Go to File > Settings > Plugins, search for "Spring Boot" and install.
   - For Eclipse: Help > Eclipse Marketplace, search for "Spring Tools" and install.

## 3. Project Structure Setup <a name="project-structure-setup"></a>

1. Open a terminal and navigate to the root of the JBoss EAP Quickstarts repository.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-kitchensink
   ```
3. Move the existing kitchensink project to a subdirectory:
   ```
   mkdir jboss-kitchensink
   mv * jboss-kitchensink/
   mv jboss-kitchensink spring-kitchensink/
   ```
   (Note: You may need to adjust this command if there are hidden files or specific files you don't want to move)

## 4. Spring Boot Project Initialization <a name="spring-boot-project-initialization"></a>

1. Navigate to the `spring-kitchensink` directory:
   ```
   cd spring-kitchensink
   ```
2. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,lombok,validation --groupId=org.jboss.as.quickstarts --artifactId=spring-kitchensink --name="Spring Kitchensink" --description="Spring Boot version of JBoss Kitchensink" --package-name=org.jboss.as.quickstarts.kitchensink .
   ```
3. This will create a new Spring Boot project in the current directory.

## 5. Version Control Setup <a name="version-control-setup"></a>

1. Ensure you're in the `spring-kitchensink` directory.
2. Initialize a new Git repository (if not already done):
   ```
   git init
   ```
3. Create a `.gitignore` file:
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
5. Stage and commit the initial Spring Boot project:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 6. Continuous Integration Setup <a name="continuous-integration-setup"></a>

1. In the `spring-kitchensink` directory, create a new directory for GitHub Actions:
   ```
   mkdir -p .github/workflows
   ```
2. Create a new YAML file for the CI workflow:
   ```
   touch .github/workflows/ci.yml
   ```
3. Add the following content to `ci.yml`:
   ```yaml
   name: Java CI with Maven

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
         run: mvn -B package --file pom.xml
   ```
4. Commit the CI configuration:
   ```
   git add .github/workflows/ci.yml
   git commit -m "Add GitHub Actions CI workflow"
   ```

With these steps completed, you have set up the development environment, initialized the Spring Boot project, configured version control, and set up basic continuous integration. You are now ready to begin the actual migration process as outlined in the migration plan.
