
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Build Tool Configuration](#build-tool-configuration)
6. [IDE Setup](#ide-setup)
7. [Final Steps](#final-steps)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:
- Administrative access to your development machine
- Internet connection
- Basic familiarity with command-line operations

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK

1. Visit the official Oracle JDK download page: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Download the appropriate JDK 21 installer for your operating system
3. Run the installer and follow the installation wizard
4. Set the JAVA_HOME environment variable:
   - Windows: Set JAVA_HOME to the JDK installation directory (e.g., C:\Program Files\Java\jdk-21)
   - macOS/Linux: Add `export JAVA_HOME=/path/to/jdk-21` to your ~/.bash_profile or ~/.zshrc file
5. Add Java to your PATH:
   - Windows: Add %JAVA_HOME%\bin to your PATH
   - macOS/Linux: Add `export PATH=$JAVA_HOME/bin:$PATH` to your profile file
6. Verify the installation by opening a new terminal and running:
   ```
   java -version
   ```

### 2.2 Install Spring Boot CLI

1. Visit the Spring Boot CLI installation page: https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli
2. Follow the installation instructions for your operating system
3. Verify the installation by opening a new terminal and running:
   ```
   spring --version
   ```

## 3. Project Initialization <a name="project-initialization"></a>

1. Navigate to the root directory of your existing JBoss project
2. Create a new directory for the Spring Boot project:
   ```
   mkdir spring-kitchensink
   cd spring-kitchensink
   ```
3. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,lombok,validation --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink --description="Migrated Kitchensink application" .
   ```

## 4. Version Control Setup <a name="version-control-setup"></a>

1. Navigate back to the root directory of your JBoss project
2. Update the existing .gitignore file to include Spring Boot specific entries:
   ```
   echo "# Spring Boot" >> .gitignore
   echo "spring-kitchensink/target/" >> .gitignore
   echo "spring-kitchensink/.mvn/" >> .gitignore
   echo "spring-kitchensink/mvnw" >> .gitignore
   echo "spring-kitchensink/mvnw.cmd" >> .gitignore
   ```
3. Stage and commit the new Spring Boot project:
   ```
   git add .
   git commit -m "Initialize Spring Boot project for migration"
   ```

## 5. Build Tool Configuration <a name="build-tool-configuration"></a>

1. Open the `spring-kitchensink/pom.xml` file in a text editor
2. Update the Java version properties:
   ```xml
   <properties>
       <java.version>21</java.version>
   </properties>
   ```
3. Add the following plugin to ensure Java 21 is used for compilation:
   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-compiler-plugin</artifactId>
               <configuration>
                   <source>21</source>
                   <target>21</target>
               </configuration>
           </plugin>
       </plugins>
   </build>
   ```
4. Save the file

## 6. IDE Setup <a name="ide-setup"></a>

### 6.1 IntelliJ IDEA

1. Open IntelliJ IDEA
2. Click on "Open" and select the root directory of your JBoss project
3. Once the project is loaded, right-click on the `spring-kitchensink` directory
4. Select "Add Framework Support"
5. Choose "Spring" and select the appropriate Spring Boot version
6. Click "OK" to apply the changes

### 6.2 Eclipse

1. Open Eclipse
2. Go to File > Import > Maven > Existing Maven Projects
3. Browse to the root directory of your JBoss project and select the `spring-kitchensink` folder
4. Click "Finish" to import the project
5. Right-click on the imported project
6. Go to Configure > Add Spring Nature

## 7. Final Steps <a name="final-steps"></a>

1. Verify the setup by running the Spring Boot application:
   ```
   cd spring-kitchensink
   ./mvnw spring-boot:run
   ```
2. Open a web browser and navigate to `http://localhost:8080`
3. If you see the default Spring Boot welcome page, your setup is complete

You are now ready to begin the migration process as outlined in the migration plan. The new Spring Boot project is set up alongside your existing JBoss application, allowing you to work on both codebases as needed during the migration.
