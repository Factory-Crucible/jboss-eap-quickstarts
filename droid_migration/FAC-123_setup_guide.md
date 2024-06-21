
# Setup Guide for JBoss 'kitchensink' to Spring Boot Migration

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Initialization](#project-initialization)
4. [Version Control Setup](#version-control-setup)
5. [Build System Configuration](#build-system-configuration)
6. [CI/CD Pipeline Setup](#cicd-pipeline-setup)
7. [Testing and Quality Assurance Tools](#testing-and-quality-assurance-tools)
8. [Cloud-Native Tools and Services](#cloud-native-tools-and-services)
9. [Database Setup](#database-setup)
10. [Optional: MongoDB Setup](#optional-mongodb-setup)

## 1. Prerequisites <a name="prerequisites"></a>

Before beginning the setup process, ensure you have accounts for the following services:
- Cloud provider (AWS, GCP, or Azure)
- Docker Hub
- GitHub or GitLab
- MongoDB Atlas (for optional MongoDB migration)

## 2. Development Environment Setup <a name="development-environment-setup"></a>

### 2.1 Install Java 21 JDK
1. Download and install Java 21 JDK from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
2. Set JAVA_HOME environment variable:
   - Windows: `setx JAVA_HOME "C:\Program Files\Java\jdk-21"`
   - macOS/Linux: `export JAVA_HOME=/path/to/jdk-21`
3. Add Java to PATH:
   - Windows: `setx PATH "%PATH%;%JAVA_HOME%\bin"`
   - macOS/Linux: `export PATH=$PATH:$JAVA_HOME/bin`

### 2.2 Install Spring Boot CLI
1. Download Spring Boot CLI from [Spring's website](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started.installing.cli)
2. Extract the archive and add the `bin` directory to your PATH

### 2.3 Install and Configure IDE
1. Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) or [Eclipse](https://www.eclipse.org/downloads/)
2. Install the Spring Boot plugin for your IDE
3. Configure Java 21 as the default JDK in your IDE

### 2.4 Install Git
1. Download and install Git from [Git's website](https://git-scm.com/downloads)
2. Configure Git with your name and email:
   ```
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```

### 2.5 Install Maven
1. Download Maven from [Maven's website](https://maven.apache.org/download.cgi)
2. Extract the archive and add the `bin` directory to your PATH
3. Verify installation: `mvn -version`

## 3. Project Initialization <a name="project-initialization"></a>

1. Go to [Spring Initializr](https://start.spring.io/)
2. Configure the project:
   - Project: Maven
   - Language: Java
   - Spring Boot: Latest stable version
   - Project Metadata: Fill in your group, artifact, and package name
   - Packaging: Jar
   - Java: 21
3. Add dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Validation
   - Lombok
   - Spring Boot DevTools
4. Generate and download the project
5. Extract the downloaded zip file to your workspace

## 4. Version Control Setup <a name="version-control-setup"></a>

1. Navigate to your project directory
2. Initialize Git repository:
   ```
   git init
   ```
3. Create `.gitignore` file:
   ```
   touch .gitignore
   ```
4. Add common Spring Boot entries to `.gitignore`:
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
5. Make initial commit:
   ```
   git add .
   git commit -m "Initial commit"
   ```

## 5. Build System Configuration <a name="build-system-configuration"></a>

1. Open `pom.xml` in your IDE
2. Ensure the Spring Boot parent POM is present:
   ```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>2.6.0</version>
   </parent>
   ```
3. Add or update necessary plugins:
   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
           </plugin>
           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-surefire-plugin</artifactId>
           </plugin>
       </plugins>
   </build>
   ```
4. Set up profiles for different environments:
   ```xml
   <profiles>
       <profile>
           <id>dev</id>
           <properties>
               <spring.profiles.active>dev</spring.profiles.active>
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

## 6. CI/CD Pipeline Setup <a name="cicd-pipeline-setup"></a>

### 6.1 GitHub Actions Setup
1. Create `.github/workflows` directory in your project root
2. Create `ci-cd.yml` file in the workflows directory
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

### 6.2 Jenkins Setup (Alternative to GitHub Actions)
1. Install Jenkins on your server or local machine
2. Create a new Jenkins job
3. Configure Source Code Management to point to your Git repository
4. Add build steps:
   ```
   mvn clean install
   mvn test
   ```
5. Configure post-build actions for test reports and artifacts

## 7. Testing and Quality Assurance Tools <a name="testing-and-quality-assurance-tools"></a>

### 7.1 JUnit 5 and Mockito Setup
1. Add dependencies to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-test</artifactId>
       <scope>test</scope>
   </dependency>
   ```

### 7.2 JaCoCo Setup
1. Add JaCoCo plugin to `pom.xml`:
   ```xml
   <plugin>
       <groupId>org.jacoco</groupId>
       <artifactId>jacoco-maven-plugin</artifactId>
       <version>0.8.7</version>
       <executions>
           <execution>
               <goals>
                   <goal>prepare-agent</goal>
               </goals>
           </execution>
           <execution>
               <id>report</id>
               <phase>prepare-package</phase>
               <goals>
                   <goal>report</goal>
               </goals>
           </execution>
       </executions>
   </plugin>
   ```

### 7.3 JMeter Setup
1. Download Apache JMeter from [JMeter's website](https://jmeter.apache.org/download_jmeter.cgi)
2. Extract the archive to a suitable location
3. Add JMeter's `bin` directory to your PATH

### 7.4 SonarQube Setup
1. Download and install SonarQube from [SonarQube's website](https://www.sonarqube.org/downloads/)
2. Start SonarQube server
3. Add SonarQube plugin to `pom.xml`:
   ```xml
   <plugin>
       <groupId>org.sonarsource.scanner.maven</groupId>
       <artifactId>sonar-maven-plugin</artifactId>
       <version>3.9.0.2155</version>
   </plugin>
   ```

## 8. Cloud-Native Tools and Services <a name="cloud-native-tools-and-services"></a>

### 8.1 Docker Setup
1. Download and install Docker from [Docker's website](https://www.docker.com/products/docker-desktop)
2. Verify installation: `docker --version`

### 8.2 Kubernetes Setup
1. Install kubectl:
   - Windows: `choco install kubernetes-cli`
   - macOS: `brew install kubectl`
   - Linux: Follow [Kubernetes documentation](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)
2. Install Minikube for local Kubernetes development:
   - Download from [Minikube's website](https://minikube.sigs.k8s.io/docs/start/)
   - Start Minikube: `minikube start`

### 8.3 Distributed Tracing Setup
1. Add Spring Cloud Sleuth and Zipkin dependencies to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-sleuth-zipkin</artifactId>
   </dependency>
   ```
2. Run Zipkin server using Docker:
   ```
   docker run -d -p 9411:9411 openzipkin/zipkin
   ```

### 8.4 ELK Stack Setup
1. Install Docker Compose
2. Create a `docker-compose.yml` file for ELK stack:
   ```yaml
   version: '3'
   services:
     elasticsearch:
       image: docker.elastic.co/elasticsearch/elasticsearch:7.15.0
       environment:
         - discovery.type=single-node
     logstash:
       image: docker.elastic.co/logstash/logstash:7.15.0
     kibana:
       image: docker.elastic.co/kibana/kibana:7.15.0
       ports:
         - "5601:5601"
   ```
3. Start ELK stack: `docker-compose up -d`

### 8.5 Prometheus and Grafana Setup
1. Add Prometheus dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>io.micrometer</groupId>
       <artifactId>micrometer-registry-prometheus</artifactId>
   </dependency>
   ```
2. Run Prometheus and Grafana using Docker:
   ```
   docker run -d -p 9090:9090 prom/prometheus
   docker run -d -p 3000:3000 grafana/grafana
   ```

## 9. Database Setup <a name="database-setup"></a>

### 9.1 Local PostgreSQL Setup
1. Download and install PostgreSQL from [PostgreSQL's website](https://www.postgresql.org/download/)
2. Create a new database for the project
3. Add PostgreSQL dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

### 9.2 Cloud PostgreSQL Setup
1. Set up a managed PostgreSQL instance in your chosen cloud provider
2. Note down the connection details (host, port, database name, username, password)
3. Update `application.yml` with cloud database configuration

## 10. Optional: MongoDB Setup <a name="optional-mongodb-setup"></a>

### 10.1 Local MongoDB Setup
1. Download and install MongoDB from [MongoDB's website](https://www.mongodb.com/try/download/community)
2. Start MongoDB service
3. Add MongoDB dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-mongodb</artifactId>
   </dependency>
   