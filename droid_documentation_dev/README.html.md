
# File: README.html

## Purpose and Description
The `README.html` file serves as the primary documentation for the `kitchensink` quickstart project within the `jboss-eap-quickstarts` repository. It provides comprehensive instructions for building, deploying, and running the `kitchensink` application, which demonstrates a Jakarta EE 10 web-enabled database application using various technologies such as JSF, CDI, EJB, JPA, and Bean Validation.

## Overview of Components

### Introduction
The introduction section provides a high-level overview of the `kitchensink` quickstart, highlighting its purpose and the technologies it demonstrates.

### What is it?
This section describes the `kitchensink` quickstart as a deployable Maven 3 project designed to help developers get started with Jakarta EE 10 on Red Hat JBoss Enterprise Application Platform. It outlines the technologies used and the application's compliance with Jakarta EE 10 standards.

### Considerations for Use in a Production Environment
This section provides important notes on the use of the H2 database and datasource configuration files, emphasizing that they are not suitable for production environments.

### System Requirements
This section lists the system requirements for building and running the `kitchensink` project, including the necessary versions of Java and Maven.

### Use of EAP_HOME and QUICKSTART_HOME Variables
This section explains the use of the `EAP_HOME` and `QUICKSTART_HOME` variables in the instructions, providing links to detailed documentation on their usage.

### Building and Running the Quickstart with a JBoss EAP Server Distribution
This section provides step-by-step instructions for starting the JBoss EAP standalone server, building and deploying the quickstart, accessing the application, and running Arquillian integration tests.

### Building and Running the Quickstart on OpenShift
This section provides instructions for building and deploying the `kitchensink` application on OpenShift using Helm charts. It includes prerequisites, deployment steps, and commands for accessing and undeploying the application.

## Usage Instructions

### Building and Running the Quickstart with a JBoss EAP Server Distribution

1. **Start the JBoss EAP Standalone Server**
   - Open a terminal and navigate to the root of the JBoss EAP directory.
   - Start the server with the default profile:
     ```bash
     $ EAP_HOME/bin/standalone.sh
     ```
   - For Windows:
     ```bash
     EAP_HOME\bin\standalone.bat
     ```
2. **Build and Deploy the Quickstart**
   - Navigate to the root directory of the quickstart.
   - Build the quickstart:
     ```bash
     $ mvn clean package
     ```
   - Deploy the quickstart:
     ```bash
     $ mvn wildfly:deploy
     ```
3. **Access the Application**
   - The application will be running at http://localhost:8080/kitchensink/.
4. **Run the Arquillian Integration Tests**
   - Ensure the server is running and the quickstart is deployed.
   - Run the tests:
     ```bash
     $ mvn verify -Parq-remote
     ```
5. **Undeploy the Quickstart**
   - Undeploy the archive:
     ```bash
     $ mvn wildfly:undeploy
     ```

### Building and Running the Quickstart on OpenShift

1. **Build the Quickstart for OpenShift**
   - Activate the `openshift` Maven profile:
     ```bash
     $ mvn clean package -Popenshift
     ```
2. **Deploy with Helm Charts**
   - Add the JBoss EAP Helm repository:
     ```bash
     $ helm repo add jboss-eap https://jbossas.github.io/eap-charts/
     ```
   - Deploy the quickstart:
     ```bash
     $ helm install kitchensink -f charts/helm.yaml jboss-eap/eap8
     ```
3. **Access the Application**
   - Get the URL of the route:
     ```bash
     $ oc get route kitchensink -o jsonpath="{.spec.host}"
     ```
   - Access the application using the displayed URL.
4. **Run the Arquillian Integration Tests**
   - Ensure the quickstart is deployed on OpenShift.
   - Run the tests:
     ```bash
     $ mvn clean verify -Parq-remote -Dserver.host=https://$(oc get route kitchensink --template='{{ .spec.host }}')
     ```
5. **Undeploy the Quickstart**
   - Uninstall the Helm release:
     ```bash
     $ helm uninstall kitchensink
     ```

## Code Samples and Snippets

### Starting the JBoss EAP Standalone Server
```bash
$ EAP_HOME/bin/standalone.sh
```

### Building and Deploying the Quickstart
```bash
$ mvn clean package
$ mvn wildfly:deploy
```

### Running Arquillian Integration Tests
```bash
$ mvn verify -Parq-remote
```

### Deploying with Helm Charts
```bash
$ helm install kitchensink -f charts/helm.yaml jboss-eap/eap8
```

### Accessing the Application on OpenShift
```bash
$ oc get route kitchensink -o jsonpath="{.spec.host}"
```

### Running Arquillian Integration Tests on OpenShift
```bash
$ mvn clean verify -Parq-remote -Dserver.host=https://$(oc get route kitchensink --template='{{ .spec.host }}')
```

### Undeploying the Quickstart
```bash
$ mvn wildfly:undeploy
$ helm uninstall kitchensink
```

## Additional Notes
- Ensure that the H2 database and `*-ds.xml` datasource configuration files are not used in production environments.
- Follow the guidelines for configuring the datasource using the Management CLI or Management Console as documented in the [Configuration Guide](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/8.0/html-single/configuration_guide/).
- The Maven profile named `openshift` is used by the Helm chart to provision the server with the quickstart deployed on the root web context, so the application should be accessed without the `/kitchensink` path segment after `HOST:PORT`.
