
# File: README.html

## Purpose and Description
The `README.html` file serves as comprehensive documentation for the `kitchensink` quickstart project. It provides detailed instructions on setting up, building, deploying, and testing the `kitchensink` application using various technologies and environments, including JBoss EAP and OpenShift.

## Overview of Components

### Introduction
The file begins with an introduction to the `kitchensink` quickstart, highlighting its purpose as a Jakarta EE 10 web-enabled database application using JSF, CDI, EJB, JPA, and Bean Validation.

### What is it?
This section describes the `kitchensink` quickstart as a deployable Maven 3 project designed to help developers get started with Jakarta EE 10 on Red Hat JBoss Enterprise Application Platform. It demonstrates the creation of a compliant Jakarta EE 10 application using various technologies and includes sample persistence and transaction code.

### Considerations for Use in a Production Environment
This section provides important notes on the use of the H2 database and datasource configuration files, emphasizing that these are for example purposes only and should not be used in production environments.

### System Requirements
Details the system requirements for running the project, including the necessary versions of Java and Maven.

### Use of EAP_HOME and QUICKSTART_HOME Variables
Explains the use of replaceable variables `<EAP_HOME>` and `<QUICKSTART_HOME>` in the instructions, providing links to further documentation.

### Building and Running the Quickstart Application with a JBoss EAP Server Distribution
Provides step-by-step instructions for starting the JBoss EAP server, building, deploying, and accessing the `kitchensink` application. It also includes instructions for running Arquillian integration tests and undeploying the quickstart.

### Building and Running the Quickstart Application with OpenShift
Details the process of building and deploying the `kitchensink` application on OpenShift using Helm Charts. It includes prerequisites, deployment steps, and instructions for running integration tests and undeploying the application.

## Usage Instructions

### Starting the JBoss EAP Standalone Server
1. Open a terminal and navigate to the root of the JBoss EAP directory.
2. Start the JBoss EAP server with the default profile:
   ```bash
   $ <EAP_HOME>/bin/standalone.sh
   ```
   For Windows:
   ```bash
   <EAP_HOME>\bin\standalone.bat
   ```

### Building and Deploying the Quickstart
1. Start the JBoss EAP server.
2. Open a terminal and navigate to the root directory of the quickstart.
3. Build the quickstart:
   ```bash
   $ mvn clean package
   ```
4. Deploy the quickstart:
   ```bash
   $ mvn wildfly:deploy
   ```

### Accessing the Application
The application will be running at:
```plaintext
http://localhost:8080/kitchensink/
```

### Running Arquillian Integration Tests
1. Start the JBoss EAP server.
2. Build and deploy the quickstart.
3. Run the integration tests:
   ```bash
   $ mvn verify -Parq-remote
   ```

### Undeploying the Quickstart
1. Start the JBoss EAP server.
2. Open a terminal and navigate to the root directory of the quickstart.
3. Undeploy the archive:
   ```bash
   $ mvn wildfly:undeploy
   ```

### Building and Deploying on OpenShift with Helm Charts
1. Log in to OpenShift and ensure Helm is installed.
2. Add the JBoss EAP Helm repository:
   ```bash
   $ helm repo add jboss-eap https://jbossas.github.io/eap-charts/
   ```
3. Navigate to the root directory of the quickstart and deploy using Helm:
   ```bash
   $ helm install kitchensink -f charts/helm.yaml jboss-eap/eap8
   ```

### Running Arquillian Integration Tests on OpenShift
1. Deploy the quickstart on OpenShift.
2. Run the integration tests:
   ```bash
   $ mvn clean verify -Parq-remote -Dserver.host=https://$(oc get route kitchensink --template='{{ .spec.host }}')
   ```

## Code Samples and Snippets

### Maven Profile for OpenShift
```xml
<profile>
    <id>openshift</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.jboss.eap.plugins</groupId>
                <artifactId>eap-maven-plugin</artifactId>
                <version>${version.eap.maven.plugin}</version>
                <configuration>
                    <feature-packs>
                        <feature-pack>
                            <location>org.jboss.eap:wildfly-ee-galleon-pack</location>
                        </feature-pack>
                        <feature-pack>
                            <location>org.jboss.eap.cloud:eap-cloud-galleon-pack</location>
                        </feature-pack>
                    </feature-packs>
                    <layers>
                        <layer>cloud-server</layer>
                    </layers>
                    <filename>ROOT.war</filename>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>package</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

## Additional Notes
- The `README.html` file is auto-generated using Asciidoctor.
- Ensure that the H2 database and `*-ds.xml` files are not used in production environments.
- Follow the guidelines for configuring Maven and JBoss EAP as provided in the linked documentation.
