
## charts
The `charts` directory is integral to the deployment process of the `Factory-Crucible/jboss-eap-quickstarts` project. It contains configuration files necessary for deploying the application using Helm, a package manager for Kubernetes. This directory ensures that deployment configurations are consistent and reliable across different environments, supporting the CI/CD practices within the project.

### Contents
The `charts` directory includes the following file:

- **`charts/helm.yaml`**: This configuration file is pivotal for deploying the application using Helm. It specifies various deployment parameters such as the source repository, branch, context directory, and the number of replicas.

### Key Components
- **`charts/helm.yaml`**: 
  - **Purpose**: This file defines the Helm deployment configuration for the application. It includes critical parameters that dictate how and where the application is deployed.
  - **Parameters**:
    - **`build`**: Specifies the build configuration for the application.
    - **`uri`**: Indicates the source repository URI from which the application is fetched.
    - **`ref`**: Defines the branch or tag in the source repository to be used.
    - **`contextDir`**: Specifies the context directory within the repository that contains the application code.
    - **`deploy`**: Contains deployment-specific configurations.
    - **`replicas`**: Indicates the number of replicas to be deployed, ensuring scalability and load balancing.

### Usage & Examples
The `charts/helm.yaml` file is utilized during the deployment phase of the software development lifecycle. It is referenced by Helm to deploy the application into a Kubernetes environment. Below is an example snippet from a typical `charts/helm.yaml` file:

```yaml
build:
  uri: https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
  ref: main
  contextDir: src/main

deploy:
  replicas: 3
```

In this example:
- The `uri` points to the source repository.
- The `ref` specifies the `main` branch.
- The `contextDir` indicates that the main application code is located in the `src/main` directory.
- The `replicas` parameter is set to `3`, meaning three instances of the application will be deployed.

This configuration ensures that the application is deployed consistently across different environments, supporting high availability and scalability. The `charts` directory, therefore, plays a crucial role in the CI/CD pipeline, facilitating automated and reliable deployments.
