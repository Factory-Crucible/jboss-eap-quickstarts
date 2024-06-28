
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prepare Development Environment](#prepare-development-environment)
2. [Set Up Version Control](#set-up-version-control)
3. [Initialize Spring Boot Project](#initialize-spring-boot-project)
4. [Configure Build Tools](#configure-build-tools)
5. [Set Up CI/CD Pipeline](#set-up-cicd-pipeline)

## 1. Prepare Development Environment <a name="prepare-development-environment"></a>

### 1.1 Install Java 21 JDK
1. Visit the official Oracle website or use a package manager to download Java 21 JDK.
2. Follow the installation instructions for your operating system.
3. Verify the installation by running `java -version` in your terminal.

### 1.2 Install Spring Boot CLI
1. Visit the official Spring Boot website to download the latest stable Spring Boot CLI.
2. Extract the downloaded archive to a directory of your choice.
3. Add the `bin` directory to your system's PATH.
4. Verify the installation by running `spring --version` in your terminal.

### 1.3 Set Up IDE
1. Download and install your preferred IDE (e.g., IntelliJ IDEA or Eclipse).
2. Install the Spring Boot plugin for your IDE:
   - For IntelliJ IDEA: Go to File > Settings > Plugins, search for "Spring Boot" and install.
   - For Eclipse: Help > Eclipse Marketplace, search for "Spring Tools" and install.

## 2. Set Up Version Control <a name="set-up-version-control"></a>

1. Open a terminal and navigate to the root directory of the existing JBoss 'kitchensink' project.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-boot-kitchensink
   ```
3. Move the existing JBoss project into a subdirectory:
   ```
   mkdir jboss-kitchensink
   mv * jboss-kitchensink/
   ```
4. Create a new `.gitignore` file in the root directory:
   ```
   touch .gitignore
   ```
5. Add the following content to the `.gitignore` file:
   ```
   # Spring Boot specific
   /target/
   !.mvn/wrapper/maven-wrapper.jar
   
   # IDE specific
   .idea/
   *.iws
   *.iml
   *.ipr
   .vscode/
   
   # Compiled class file
   *.class
   
   # Log file
   *.log
   
   # Package Files #
   *.jar
   *.war
   *.nar
   *.ear
   *.zip
   *.tar.gz
   *.rar
   
   # virtual machine crash logs
   hs_err_pid*
   ```
6. Commit the changes:
   ```
   git add .
   git commit -m "Restructure project for Spring Boot migration"
   ```

## 3. Initialize Spring Boot Project <a name="initialize-spring-boot-project"></a>

1. Navigate to the `spring-boot-kitchensink` directory:
   ```
   cd spring-boot-kitchensink
   ```
2. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,lombok,validation --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink --description="Kitchensink Spring Boot Migration" .
   ```
3. Review the generated project structure and `pom.xml` file.

## 4. Configure Build Tools <a name="configure-build-tools"></a>

1. Ensure the `pom.xml` file in the `spring-boot-kitchensink` directory contains the necessary dependencies and plugins.
2. Add the Maven Wrapper to the project:
   ```
   mvn -N io.takari:maven:wrapper
   ```
3. Verify the Maven Wrapper installation:
   ```
   ./mvnw --version
   ```

## 5. Set Up CI/CD Pipeline <a name="set-up-cicd-pipeline"></a>

1. Create a `.github/workflows` directory in the root of your project:
   ```
   mkdir -p .github/workflows
   ```
2. Create a new file named `ci-cd.yml` in the `.github/workflows` directory:
   ```
   touch .github/workflows/ci-cd.yml
   ```
3. Add the following content to the `ci-cd.yml` file:
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
4. Commit the changes:
   ```
   git add .
   git commit -m "Add CI/CD configuration"
   ```
5. Push the changes to your GitHub repository:
   ```
   git push origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now ready, the project structure is set up, version control is configured, build tools are in place, and a basic CI/CD pipeline is established. You can now proceed with the actual migration steps as outlined in the migration plan.
