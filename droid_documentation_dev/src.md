
## src

The `src` directory is the foundational structure for the project's source code and testing framework. It includes `src/main` for the main source code and resources, and `src/test` for the testing infrastructure. `src/main` contains subdirectories for web pages, configuration files, and core components. `src/test` includes resources for an isolated test environment and integration tests for core features. Together, these subdirectories ensure proper organization, configuration, functionality, and testing of the web application.

### Contents

- **src/main**: Central hub for the project's main source code and resources.
  - **src/main/java**: Foundational package for the JBoss AS project, containing essential subprojects and components.
    - **src/main/java/org**: Foundational package for the JBoss AS project, containing essential subprojects and components.
      - **src/main/java/org/jboss**: Foundational package in the JBoss AS project, containing essential subprojects and components.
        - **src/main/java/org/jboss/as**: Foundational package in the JBoss AS project, containing subprojects and components like the `quickstarts` directory.
          - **src/main/java/org/jboss/as/quickstarts**: Root package for the JBoss AS Quickstarts project, providing example applications and code snippets.
            - **src/main/java/org/jboss/as/quickstarts/kitchensink**: Core package for the JBoss Kitchensink quickstart project.
              - **src/main/java/org/jboss/as/quickstarts/kitchensink/controller**: Controller layer in a Jakarta EE application.
                - **MemberController.java**: Manages member registration by interacting with the MemberRegistration service and handling JSF messages.
              - **src/main/java/org/jboss/as/quickstarts/kitchensink/data**: Contains classes for data access and manipulation of the `Member` entity.
                - **MemberListProducer.java**: Produces a list of `Member` objects for the UI layer.
                - **MemberRepository.java**: Provides methods to interact with the Member entity in the database.
              - **src/main/java/org/jboss/as/quickstarts/kitchensink/model**: Contains the `Member.java` class, a JPA entity representing a member in the application.
                - **Member.java**: Defines a Java class named `Member` which is part of the package `org.jboss.as.quickstarts.kitchensink.model`.
              - **src/main/java/org/jboss/as/quickstarts/kitchensink/rest**: Contains Java classes that implement RESTful web services using JAX-RS.
                - **JaxRsActivator.java**: Activates JAX-RS resources to be served relative to the /rest path.
                - **MemberResourceRESTService.java**: Provides endpoints to interact with the `Member` entity, allowing clients to perform CRUD operations.
              - **src/main/java/org/jboss/as/quickstarts/kitchensink/service**: Focuses on the business logic for member registration.
                - **MemberRegistration.java**: Registers new members by persisting their data to the database and firing events to notify other components.
              - **src/main/java/org/jboss/as/quickstarts/kitchensink/util**: Contains utility classes for managing Jakarta EE resources using CDI.
                - **Resources.java**: Provides an `EntityManager` and a `Logger` for injection into other managed beans.
  - **src/main/resources**: Contains essential configuration files and resources.
    - **META-INF**: Configures Jakarta Persistence (JPA) in the Java application.
      - **persistence.xml**: Defines the persistence unit, data source, and Hibernate properties for managing database interactions.
    - **import.sql**: Used to load seed data into a database.
  - **src/main/webapp**: Contains essential files and subdirectories for rendering web pages, managing resources, and ensuring consistent application behavior.
    - **WEB-INF**: Configures and manages the Java EE web application.
      - **beans.xml**: Enables Contexts and Dependency Injection (CDI) in a Java EE application.
      - **faces-config.xml**: Configuration file for JavaServer Faces (JSF) applications.
      - **kitchensink-quickstart-ds.xml**: Configures an unmanaged datasource named `KitchensinkQuickstartDS` for a JBoss application server.
      - **templates**: Contains XHTML templates for a JSF web application.
        - **default.xhtml**: Defines the structure and layout of a web page.
    - **index.html**: Simple HTML document that immediately redirects the user to 'index.jsf'.
    - **index.xhtml**: Jakarta Faces (JSF) Facelets template used for rendering a web page.
    - **resources**: Repository for static resources used by the web application.
      - **css**: Contains CSS stylesheets that define the visual presentation of the web pages.
        - **screen.css**: CSS stylesheet for styling a web page.
- **src/test**: Contains the project's testing infrastructure.
  - **src/test/java**: Contains integration tests for the JBoss Quickstarts project.
    - **org**: Contains test code for the JBoss project.
      - **jboss**: Contains test code for the JBoss project.
        - **as**: Contains test code for the JBoss AS project.
          - **quickstarts**: Contains integration tests for the JBoss Quickstarts project.
            - **kitchensink**: Dedicated to integration testing within the JBoss Kitchensink application.
              - **test**: Contains integration test classes for the JBoss Kitchensink application.
                - **MemberRegistrationIT.java**: Integration test class for the `MemberRegistration` service.
                - **RemoteMemberRegistrationIT.java**: JUnit test class for testing the remote member registration functionality.
  - **src/test/resources**: Contains configuration files for an isolated test environment.
    - **META-INF**: Configures a persistence unit named 'primary' for testing with an in-memory H2 database.
      - **test-persistence.xml**: Configures a persistence unit named 'primary' for testing purposes.
    - **arquillian.xml**: Configuration file for Arquillian.
    - **test-ds.xml**: XML configuration file for an unmanaged datasource used for testing purposes.

### Key Components

- **MemberController.java**: Manages member registration by interacting with the MemberRegistration service and handling JSF messages.
- **MemberListProducer.java**: Produces a list of `Member` objects for the UI layer.
- **MemberRepository.java**: Provides methods to interact with the Member entity in the database.
- **Member.java**: Defines a Java class named `Member` which is part of the package `org.jboss.as.quickstarts.kitchensink.model`.
- **JaxRsActivator.java**: Activates JAX-RS resources to be served relative to the /rest path.
- **MemberResourceRESTService.java**: Provides endpoints to interact with the `Member` entity, allowing clients to perform CRUD operations.
- **MemberRegistration.java**: Registers new members by persisting their data to the database and firing events to notify other components.
- **Resources.java**: Provides an `EntityManager` and a `Logger` for injection into other managed beans.
- **persistence.xml**: Defines the persistence unit, data source, and Hibernate properties for managing database interactions.
- **import.sql**: Used to load seed data into a database.
- **beans.xml**: Enables Contexts and Dependency Injection (CDI) in a Java EE application.
- **faces-config.xml**: Configuration file for JavaServer Faces (JSF) applications.
- **kitchensink-quickstart-ds.xml**: Configures an unmanaged datasource named `KitchensinkQuickstartDS` for a JBoss application server.
- **default.xhtml**: Defines the structure and layout of a web page.
- **screen.css**: CSS stylesheet for styling a web page.
- **MemberRegistrationIT.java**: Integration test class for the `MemberRegistration` service.
- **RemoteMemberRegistrationIT.java**: JUnit test class for testing the remote member registration functionality.
- **test-persistence.xml**: Configures a persistence unit named 'primary' for testing purposes.
- **arquillian.xml**: Configuration file for Arquillian.
- **test-ds.xml**: XML configuration file for an unmanaged datasource used for testing purposes.

### Usage & Examples

The `src` directory is organized to facilitate the development, configuration, and testing of the JBoss Kitchensink application. The `src/main` subdirectory contains the main source code, organized into packages for controllers, data access, data models, RESTful services, business logic, and utilities. This modular structure ensures that each component is well-defined and can be developed and maintained independently.

For example, the `MemberController.java` file in the `controller` package manages member registration by interacting with the `MemberRegistration` service. It initializes a new `Member` object upon construction, registers a new member, and handles success or failure messages. This separation of concerns allows for clear and maintainable code.

The `src/test` subdirectory contains integration tests that validate the core functionality of the application. The `MemberRegistrationIT.java` file uses the Arquillian framework to deploy a test archive and run the test in a container. The test verifies that a new `Member` can be registered and persisted correctly, ensuring the reliability and stability of the application.

The `src/main/resources` and `src/test/resources` directories contain configuration files that manage database interactions and testing environments. The `persistence.xml` file in `src/main/resources/META-INF` defines the persistence unit and data source for the application, while the `test-persistence.xml` file in `src/test/resources/META-INF` configures a persistence unit for testing with an in-memory H2 database. These configurations ensure proper database initialization and data persistence management in both development and testing environments.

Overall, the `src` directory is structured to support the development, configuration, and testing of the JBoss Kitchensink application, ensuring a robust and maintainable codebase.
