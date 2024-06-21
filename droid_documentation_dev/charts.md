
## charts
The `charts` directory is integral to the deployment process of the `jboss-eap-quickstarts` repository. It contains configuration files necessary for deploying the application using Helm, a package manager for Kubernetes. This directory ensures that deployment configurations are managed efficiently, promoting consistent and reliable application deployment across various environments. By leveraging Helm, the `charts` directory supports the deployment phase of the software development lifecycle, facilitating Continuous Integration and Continuous Deployment (CI/CD) practices within a Kubernetes environment.

### Contents
The `charts` directory includes the following file:

- **charts/helm.yaml**: This configuration file is pivotal for deploying the application using Helm. It specifies critical parameters such as the source repository, branch, context directory, and the number of replicas for the deployment. These parameters are essential for defining how the application should be deployed and scaled within a Kubernetes cluster.

### Key Components
- **charts/helm.yaml**: The `helm.yaml` file is the cornerstone of the `charts` directory. It contains the necessary configuration for Helm to deploy the application. The key components of this file include:
  - **build**: Specifies the build configuration for the application.
  - **uri**: Defines the source repository URI from which the application code is fetched.
  - **ref**: Indicates the branch or tag of the source repository to be used.
  - **contextDir**: Specifies the context directory within the repository that contains the application code.
  - **deploy**: Configuration settings related to the deployment process.
  - **replicas**: Defines the number of replicas to be deployed, ensuring scalability and high availability of the application.

### Usage & Examples
The `charts/helm.yaml` file is used to manage the deployment of the `jboss-eap-quickstarts` application within a Kubernetes environment. Below is an example snippet from a typical `helm.yaml` file:

```yaml
build:
  uri: https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
  ref: main
  contextDir: src/main
deploy:
  replicas: 3
```

In this example:
- The `uri` points to the GitHub repository containing the application code.
- The `ref` specifies that the `main` branch should be used for the deployment.
- The `contextDir` indicates that the source code is located in the `src/main` directory.
- The `replicas` parameter is set to `3`, meaning three instances of the application will be deployed to ensure load balancing and fault tolerance.

By configuring these parameters, the `charts/helm.yaml` file enables seamless deployment and scaling of the application, aligning with best practices for managing applications in a Kubernetes environment.
