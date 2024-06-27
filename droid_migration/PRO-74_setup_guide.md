
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Project Structure Setup](#project-structure-setup)
6. [Build and Dependency Configuration](#build-and-dependency-configuration)
7. [Application Properties Configuration](#application-properties-configuration)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have the following:
- Administrator access to your development machine
- Internet connection
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
2. Follow the instructions for your operating system to download and install Spring Boot CLI
3. Verify the installation by opening a terminal and running:
   ```
   spring --version
   ```
   You should see output indicating the Spring Boot CLI version

### 2.3 Set up IDE

1. Download and install your preferred IDE (e.g., IntelliJ IDEA or Eclipse)
2. Install the Spring Boot plugin for your IDE:
   - For IntelliJ IDEA: Go to File > Settings > Plugins, search for "Spring Boot" and install
   - For Eclipse: Help > Eclipse Marketplace, search for "Spring Tools" and install

## 3. Project Initialization <a name="project-initialization"></a>

1. Open a terminal and navigate to the root of the jboss-eap-quickstarts repository
2. Create a new directory for the Spring Boot project:
   ```
   mkdir kitchensink-spring
   cd kitchensink-spring
   ```
3. Use Spring Initializr to create a new Spring Boot project:
   ```
   spring init --build=maven --java-version=21 --dependencies=web,data-jpa,h2,devtools,lombok,validation --groupId=com.example --artifactId=kitchensink --name=kitchensink --package-name=com.example.kitchensink kitchensink
   ```
4. Move the generated files to the current directory:
   ```
   mv kitchensink/* .
   rmdir kitchensink
   ```

## 4. Version Control Setup <a name="version-control-setup"></a>

1. Create a `.gitignore` file in the `kitchensink-spring` directory:
   ```
   touch .gitignore
   ```
2. Add the following content to `.gitignore`:
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
3. Initialize a new Git repository for the Spring Boot project:
   ```
   git init
   git add .
   git commit -m "Initial Spring Boot project setup"
   ```

## 5. Project Structure Setup <a name="project-structure-setup"></a>

1. Create the following package structure in `src/main/java/com/example/kitchensink`:
   ```
   mkdir -p src/main/java/com/example/kitchensink/{controller,repository,model,service,config}
   ```
2. Create placeholder files for each package:
   ```
   touch src/main/java/com/example/kitchensink/controller/.gitkeep
   touch src/main/java/com/example/kitchensink/repository/.gitkeep
   touch src/main/java/com/example/kitchensink/model/.gitkeep
   touch src/main/java/com/example/kitchensink/service/.gitkeep
   touch src/main/java/com/example/kitchensink/config/.gitkeep
   ```

## 6. Build and Dependency Configuration <a name="build-and-dependency-configuration"></a>

1. Open `pom.xml` in your IDE or text editor
2. Ensure the following dependencies are present under the `<dependencies>` section:
   ```xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-data-jpa</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-validation</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-devtools</artifactId>
           <scope>runtime</scope>
           <optional>true</optional>
       </dependency>
       <dependency>
           <groupId>com.h2database</groupId>
           <artifactId>h2</artifactId>
           <scope>runtime</scope>
       </dependency>
       <dependency>
           <groupId>org.projectlombok</groupId>
           <artifactId>lombok</artifactId>
           <optional>true</optional>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-test</artifactId>
           <scope>test</scope>
       </dependency>
   </dependencies>
   ```
3. Ensure the Spring Boot Maven plugin is present under the `<build>` section:
   ```xml
   <build>
       <plugins>
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

## 7. Application Properties Configuration <a name="application-properties-configuration"></a>

1. Open `src/main/resources/application.properties`
2. Add the following configuration:
   ```
   # Server port
   server.port=8080

   # H2 Database
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

   # JPA/Hibernate
   spring.jpa.show-sql=true
   spring.jpa.hibernate.ddl-auto=update

   # Logging
   logging.level.org.springframework=INFO
   logging.level.com.example.kitchensink=DEBUG
   ```

With these steps completed, your development environment is set up and ready for the migration process. The new Spring Boot project is initialized within the existing repository, maintaining the original codebase while providing a structure for the migrated application.
