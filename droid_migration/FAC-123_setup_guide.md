
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [IDE Configuration](#ide-configuration)
4. [Project Initialization](#project-initialization)
5. [Version Control Setup](#version-control-setup)
6. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- GitHub account (for repository access and CI/CD setup)

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page or use an open-source alternative like AdoptOpenJDK.
2. Download the Java 21 JDK installer for your operating system.
3. Run the installer and follow the installation wizard.
4. Set the JAVA_HOME environment variable:
   - Windows: Set JAVA_HOME to the JDK installation directory in System Properties > Environment Variables.
   - macOS/Linux: Add `export JAVA_HOME=/path/to/jdk21` to your `~/.bash_profile` or `~/.zshrc` file.
5. Add Java to your PATH:
   - Windows: Add `%JAVA_HOME%\bin` to the PATH variable.
   - macOS/Linux: Add `export PATH=$JAVA_HOME/bin:$PATH` to your profile file.
6. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 2.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page.
2. Download the latest stable version of Spring Boot CLI.
3. Extract the archive to a directory of your choice.
4. Add the `bin` directory to your PATH:
   - Windows: Add the full path to the `bin` directory to the PATH variable.
   - macOS/Linux: Add `export PATH=/path/to/spring-boot-cli/bin:$PATH` to your profile file.
5. Verify the installation by opening a new terminal and running:
   ```
   spring --version
   ```

### 2.3 Install Git

1. Visit the official Git website.
2. Download and install Git for your operating system.
3. Verify the installation by opening a new terminal and running:
   ```
   git --version
   ```

## 3. IDE Configuration <a name="ide-configuration"></a>

This guide uses IntelliJ IDEA as an example. Adjust the steps accordingly if using a different IDE.

1. Download and install IntelliJ IDEA (Community or Ultimate edition).
2. Launch IntelliJ IDEA.
3. Install the "Spring Boot" plugin:
   - Go to File > Settings (on Windows/Linux) or IntelliJ IDEA > Preferences (on macOS).
   - Navigate to Plugins.
   - Search for "Spring Boot" and install the plugin.
   - Restart IntelliJ IDEA when prompted.

## 4. Project Initialization <a name="project-initialization"></a>

1. Clone the existing repository:
   ```
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts
   ```

2. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring-boot
   cd kitchensink-spring-boot
   ```

3. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,validation,lombok,devtools --groupId=com.example --artifactId=kitchensink --name=kitchensink --description="Kitchensink Spring Boot Migration" --package-name=com.example.kitchensink .
   ```

4. Open the newly created project in IntelliJ IDEA:
   - Go to File > Open.
   - Navigate to the `kitchensink-spring-boot` directory and select it.
   - Click "OK" to open the project.

5. Verify the project structure and dependencies in the `pom.xml` file.

## 5. Version Control Setup <a name="version-control-setup"></a>

1. Initialize a new Git repository for the Spring Boot project:
   ```
   git init
   ```

2. Create a `.gitignore` file:
   ```
   touch .gitignore
   ```

3. Add the following content to the `.gitignore` file:
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

4. Stage and commit the initial project files:
   ```
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 6. CI/CD Pipeline Configuration <a name="cicd-pipeline-configuration"></a>

We'll use GitHub Actions for CI/CD. Follow these steps to set up a basic pipeline:

1. In the `kitchensink-spring-boot` directory, create a new directory structure for GitHub Actions:
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
   git commit -m "Add CI/CD pipeline configuration"
   ```

5. Push the changes to the remote repository:
   ```
   git remote add origin https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   git push -u origin main
   ```

This completes the setup process for the JBoss 'kitchensink' to Spring Boot migration project. The development environment is now ready, and you can proceed with the actual migration steps as outlined in the migration plan.
