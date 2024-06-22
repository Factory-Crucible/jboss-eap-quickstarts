
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Build Tool Configuration](#build-tool-configuration)
6. [Project Structure Setup](#project-structure-setup)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following tools installed on your system:

- Git
- Java 21 JDK
- Maven
- Node.js and npm (for frontend development in later phases)
- Docker (for containerization in later phases)

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Download Java 21 JDK from the official Oracle website or use OpenJDK.
2. Install the JDK following the instructions for your operating system.
3. Set the JAVA_HOME environment variable to point to your Java 21 installation.
4. Add the Java bin directory to your system PATH.

### 2.2 Install Spring Boot CLI

1. Download the latest Spring Boot CLI from the official Spring website.
2. Extract the archive to a directory of your choice.
3. Add the bin directory of the extracted archive to your system PATH.

### 2.3 Set up IDE

1. Download and install either IntelliJ IDEA or Eclipse.
2. For IntelliJ IDEA:
   - Install the "Spring Assistant" plugin from the JetBrains Marketplace.
3. For Eclipse:
   - Install the "Spring Tools 4" plugin from the Eclipse Marketplace.

## 3. Project Initialization <a name="project-initialization"></a>

### 3.1 Use Spring Initializr

1. Open a web browser and navigate to https://start.spring.io/
2. Configure the project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Latest stable version
   - Project Metadata:
     - Group: com.example
     - Artifact: kitchensink
     - Name: kitchensink
     - Description: Migrated Kitchensink application
     - Package name: com.example.kitchensink
   - Packaging: Jar
   - Java: 21
3. Add the following dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Boot DevTools
   - Lombok
   - Validation
   - Spring Boot Actuator
4. Click "Generate" to download the project ZIP file.

## 4. Version Control Setup <a name="version-control-setup"></a>

1. Open a terminal and navigate to the root directory of the existing JBoss project.
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-kitchensink
   ```
3. Move into the new directory:
   ```
   cd spring-kitchensink
   ```
4. Initialize a new Git repository:
   ```
   git init
   ```
5. Create a .gitignore file:
   ```
   touch .gitignore
   ```
6. Open the .gitignore file in a text editor and add the following content:
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
7. Make the initial commit:
   ```
   git add .
   git commit -m "Initial commit: Spring Boot project structure"
   ```

## 5. Build Tool Configuration <a name="build-tool-configuration"></a>

1. Extract the contents of the downloaded Spring Initializr ZIP file into the `spring-kitchensink` directory.

2. Set up the Maven wrapper:
   ```
   mvn wrapper:wrapper
   ```

3. Open the `pom.xml` file in a text editor and add the following plugins under the `<build>` section:
   ```xml
   <build>
     <plugins>
       <!-- Existing plugins -->
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-compiler-plugin</artifactId>
         <configuration>
           <source>21</source>
           <target>21</target>
         </configuration>
       </plugin>
       <plugin>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-maven-plugin</artifactId>
         <configuration>
           <excludes>
             <exclude>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
             </exclude>
           </excludes>
         </configuration>
       </plugin>
     </plugins>
   </build>
   ```

4. Add profiles for different environments by adding the following section to the `pom.xml`:
   ```xml
   <profiles>
     <profile>
       <id>dev</id>
       <activation>
         <activeByDefault>true</activeByDefault>
       </activation>
       <properties>
         <spring.profiles.active>dev</spring.profiles.active>
       </properties>
     </profile>
     <profile>
       <id>test</id>
       <properties>
         <spring.profiles.active>test</spring.profiles.active>
       </properties>
     </profile>
     <profile>
       <id>prod</id>
       <properties>
         <spring.profiles.active>prod</spring.profiles.active>
       </properties>
     </profile>
   </profiles>
   ```

5. Save the `pom.xml` file.

## 6. Project Structure Setup <a name="project-structure-setup"></a>

1. In the root directory of the existing JBoss project, create a new folder for the old codebase:
   ```
   mkdir jboss-kitchensink
   ```

2. Move all existing files and folders (except `spring-kitchensink`) into the `jboss-kitchensink` folder:
   ```
   mv * jboss-kitchensink/
   ```

3. Update the main README.md file in the root directory to reflect the new project structure and migration process.

4. Commit the changes:
   ```
   git add .
   git commit -m "Restructure project: Separate JBoss and Spring Boot codebases"
   ```

Your development environment is now set up and ready for the migration process. The next steps will involve migrating the actual code and functionality from the JBoss application to the new Spring Boot project structure.
