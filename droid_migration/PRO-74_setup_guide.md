
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
     export JAVA_HOME=/usr/lib/jvm/java-21-oracle
     ```
5. Add Java to your PATH:
   - Windows: 
     ```
     setx PATH "%PATH%;%JAVA_HOME%\bin"
     ```
   - macOS/Linux:
     ```
     export PATH=$PATH:$JAVA_HOME/bin
     ```
6. Verify the installation by opening a new terminal and running:
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
     export PATH=$PATH:/path/to/spring-boot-cli/bin
     ```
5. Verify the installation by opening a new terminal and running:
   ```
   spring --version
   ```

### 1.3 Set Up IDE with Spring Boot Support

This guide assumes you're using IntelliJ IDEA, but you can use any IDE with Spring Boot support.

1. Download and install IntelliJ IDEA from: https://www.jetbrains.com/idea/download/
2. During installation, ensure you select the option to include Java development tools.
3. Once installed, open IntelliJ IDEA.
4. Go to `File > Settings` (on Windows/Linux) or `IntelliJ IDEA > Preferences` (on macOS).
5. Navigate to `Plugins`.
6. Search for "Spring Boot" and install the "Spring Boot Assistant" plugin.
7. Restart IntelliJ IDEA after the plugin installation.

## 2. Project Initialization

### 2.1 Create New Spring Boot Project

1. Open your web browser and go to https://start.spring.io/
2. Configure the project as follows:
   - Project: Maven
   - Language: Java
   - Spring Boot: (Select the latest stable version)
   - Project Metadata:
     - Group: com.example
     - Artifact: kitchensink-spring
     - Name: kitchensink-spring
     - Description: Kitchensink application migrated to Spring Boot
     - Package name: com.example.kitchensink
     - Packaging: Jar
     - Java: 21
3. Add the following dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Boot DevTools
   - Spring Boot Actuator
   - Lombok
4. Click "Generate" to download the project zip file.

### 2.2 Import Project into IDE

1. Extract the downloaded zip file.
2. Open IntelliJ IDEA.
3. Click on "Open" and navigate to the extracted project folder.
4. Select the folder and click "OK".
5. If prompted, select "Open as Project".
6. Wait for IntelliJ to import and index the project.

## 3. Version Control Setup

### 3.1 Prepare Repository Structure

1. Open a terminal and navigate to your local clone of the jboss-eap-quickstarts repository.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   ```
3. Move the original kitchensink project into a subdirectory:
   ```
   mkdir kitchensink-jboss
   mv * kitchensink-jboss/
   ```
   (Note: This command moves all files and directories. You may need to move hidden files separately.)
4. Move the extracted Spring Boot project into the kitchensink-spring directory:
   ```
   mv /path/to/extracted/kitchensink-spring/* kitchensink-spring/
   ```

### 3.2 Update .gitignore

1. Open the `.gitignore` file in the root of the repository.
2. Add the following entries:
   ```
   # Spring Boot
   kitchensink-spring/HELP.md
   kitchensink-spring/target/
   kitchensink-spring/.mvn/
   kitchensink-spring/mvnw
   kitchensink-spring/mvnw.cmd
   ```

### 3.3 Commit Changes

1. Stage the changes:
   ```
   git add .
   ```
2. Commit the changes:
   ```
   git commit -m "Set up project structure for Spring Boot migration"
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
2. Create a new file named `ci-cd.yml` in the `.github/workflows` directory.
3. Open the `ci-cd.yml` file and add the following content:

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
        cd kitchensink-spring
        mvn clean package

    - name: Run tests
      run: |
        cd kitchensink-spring
        mvn test

    - name: Upload artifact
      uses: actions/upload-artifact@v2
      with:
        name: kitchensink-spring
        path: kitchensink-spring/target/*.jar
```

4. Commit and push the changes:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add CI/CD pipeline configuration"
   git push origin main
   ```

### 4.2 Configure Staging and Production Environments

For this setup guide, we'll assume you're using Heroku for staging and production environments. You'll need to have a Heroku account and the Heroku CLI installed.

1. Install Heroku CLI:
   - Follow the instructions at: https://devcenter.heroku.com/articles/heroku-cli

2. Log in to Heroku:
   ```
   heroku login
   ```

3. Create staging app:
   ```
   heroku create kitchensink-spring-staging
   ```

4. Create production app:
   ```
   heroku create kitchensink-spring-production
   ```

5. Add Heroku deployment to GitHub Actions:
   - Open `.github/workflows/ci-cd.yml`
   - Add the following jobs after the `build` job:

```yaml
  deploy-staging:
    needs: build
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - uses: akhileshns/heroku-deploy@v3.12.12
      with:
        heroku_api_key: ${{secrets.HEROKU_API_KEY}}
        heroku_app_name: "kitchensink-spring-staging"
        heroku_email: "your-email@example.com"
        appdir: "kitchensink-spring"

  deploy-production:
    needs: [build, deploy-staging]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - uses: actions/checkout@v2
    - uses: akhileshns/heroku-deploy@v3.12.12
      with:
        heroku_api_key: ${{secrets.HEROKU_API_KEY}}
        heroku_app_name: "kitchensink-spring-production"
        heroku_email: "your-email@example.com"
        appdir: "kitchensink-spring"
```

6. Set up Heroku API key as a secret in GitHub:
   - Go to your GitHub repository
   - Click on "Settings" > "Secrets"
   - Click "New repository secret"
   - Name: HEROKU_API_KEY
   - Value: (Your Heroku API key, which you can find in your Heroku account settings)

7. Commit and push the changes:
   ```
   git add .github/workflows/ci-cd.yml
   git commit -m "Add Heroku deployment to CI/CD pipeline"
   git push origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. You now have a development environment set up, a new Spring Boot project initialized within the existing repository structure, version control configured, and a CI/CD pipeline set up with GitHub Actions and Heroku deployment.
