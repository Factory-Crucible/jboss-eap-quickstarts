
## src Directory
The `src` directory is the foundational structure for the project's source code and testing framework. It includes `src/main` for the main source code and resources, and `src/test` for the testing infrastructure. `src/main` contains subdirectories for web pages, configuration files, and core components. `src/test` includes resources for an isolated test environment and integration tests for core features. Together, these subdirectories ensure proper organization, configuration, functionality, and testing of the web application.

### Contents
The `src` directory is organized into two primary subdirectories:

- **`src/main`**: Contains the main source code and resources.
  - **`src/main/java`**: Core components and example applications.
  - **`src/main/resources`**: Configuration files and resources.
  - **`src/main/webapp`**: Web application files and resources.
- **`src/test`**: Testing infrastructure.
  - **`src/test/java`**: Integration tests for core features.
  - **`src/test/resources`**: Configuration files for isolated test environments.

### Key Components
#### src/main/java
The `src/main/java` directory is a foundational package for the JBoss AS project, containing essential subprojects and components. The primary subdirectory, `org`, includes various subcomponents and example applications, such as the `quickstarts` directory with its `kitchensink` subdirectory. These elements demonstrate the capabilities and best practices within the JBoss ecosystem.

#### src/main/resources
The `src/main/resources` directory is crucial for the application's functionality, containing essential configuration files and resources. It includes `import.sql` for loading seed data into the database and the `META-INF` subdirectory for configuring Jakarta Persistence (JPA) with `persistence.xml`. These elements ensure proper database initialization and data persistence management.

#### src/main/webapp
The `src/main/webapp` directory is integral to the web application, containing essential files and subdirectories for rendering web pages, managing resources, and ensuring consistent application behavior. It includes `index.xhtml` for the main web page, `index.html` for redirection, `resources` for static assets like CSS, and `WEB-INF` for configuration files and templates.

#### src/test/java
The `src/test/java` directory contains integration tests for the JBoss Quickstarts project, ensuring core features like member registration in the JBoss Kitchensink application work correctly in a deployed environment. This contributes to the project's reliability and stability.

#### src/test/resources
The `src/test/resources` directory is essential for the project's testing infrastructure. It contains configuration files like `test-ds.xml` for an unmanaged datasource using an in-memory H2 database, and `arquillian.xml` for setting up a managed JBoss EAP instance with Arquillian. The `META-INF` subdirectory includes `test-persistence.xml`, configuring a persistence unit for testing. These resources ensure an isolated, reproducible test environment that avoids interference with production data.

### Usage & Examples
#### src/main/java/org/jboss/as/quickstarts/kitchensink/controller/MemberController.java
The `MemberController.java` file is a Java class that serves as a controller in a Jakarta EE application. It is part of the `org.jboss.as.quickstarts.kitchensink.controller` package. The class is annotated with `@Model`, making it a request-scoped bean with an EL name. It manages the registration of new members by interacting with the `MemberRegistration` service and the `FacesContext` for JSF messages. Key functionalities include initializing a new `Member` object upon construction, registering a new member and handling success or failure messages, and extracting the root cause message from exceptions.

```java
class MemberController {
    private FacesContext facesContext;
    private MemberRegistration memberRegistration;
    private Member newMember;

    @PostConstruct
    public void initNewMember() {
        // Initialization logic
    }

    public void register() throws Exception {
        // Registration logic
    }

    private String getRootErrorMessage(Exception e) {
        // Error handling logic
    }
}
```

#### src/main/java/org/jboss/as/quickstarts/kitchensink/data/MemberListProducer.java
The `MemberListProducer.java` file is responsible for producing a list of `Member` objects that can be accessed in the UI layer. The class `MemberListProducer` is annotated with `@RequestScoped`, meaning its lifecycle is tied to a single HTTP request. It uses dependency injection to obtain an instance of `MemberRepository` to fetch the list of members. The `@Produces` and `@Named` annotations expose the list of members to the UI under the name "members". The `onMemberListChanged` method listens for changes to the `Member` entity and updates the list accordingly. The `retrieveAllMembersOrderedByName` method is called after construction to initialize the list of members.

```java
class MemberListProducer {
    @Produces
    @Named
    private List<Member> members;

    @Inject
    private MemberRepository memberRepository;

    @PostConstruct
    public void retrieveAllMembersOrderedByName() {
        members = memberRepository.findAllOrderedByName();
    }

    public void onMemberListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Member member) {
        retrieveAllMembersOrderedByName();
    }
}
```

#### src/main/java/org/jboss/as/quickstarts/kitchensink/model/Member.java
The `Member.java` file defines a Java class named `Member` which is part of the package `org.jboss.as.quickstarts.kitchensink.model`. This class is annotated as a JPA entity and is mapped to a database table with a unique constraint on the `email` column. The `Member` class implements `Serializable` and contains fields for `id`, `name`, `email`, and `phoneNumber`, each with appropriate validation constraints. The class provides getter and setter methods for these fields.

```java
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Member implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 12)
    private String phoneNumber;

    // Getters and setters
}
```

#### src/main/java/org/jboss/as/quickstarts/kitchensink/rest/MemberResourceRESTService.java
The `MemberResourceRESTService.java` file is a RESTful web service implementation in Java using JAX-RS. It provides endpoints to interact with the `Member` entity, allowing clients to perform CRUD operations. The class is annotated with `@Path("/members")` to define the base URI for all resource URIs provided by this service. It includes methods to list all members, look up a member by ID, and create a new member. The class uses dependency injection to obtain instances of `Logger`, `Validator`, `MemberRepository`, and `MemberRegistration`. It also includes validation logic to ensure that the member data adheres to defined constraints and checks for the uniqueness of the email address.

```java
@Path("/members")
public class MemberResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private MemberRepository repository;

    @Inject
    private MemberRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Member> listAllMembers() {
        return repository.findAllOrderedByName();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Member lookupMemberById(@PathParam("id") long id) {
        return repository.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMember(Member member) {
        Response.ResponseBuilder builder = null;

        try {
            validateMember(member);
            registration.register(member);
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(getRootErrorMessage(e));
        }

        return builder.build();
    }

    private void validateMember(Member member) throws ConstraintViolationException {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }

    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    private String getRootErrorMessage(Exception e) {
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            return errorMessage;
        }

        Throwable t = e;
        while (t != null) {
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        return errorMessage;
    }
}
```

#### src/test/java/org/jboss/as/quickstarts/kitchensink/test/MemberRegistrationIT.java
The `MemberRegistrationIT.java` file is an integration test class for the `MemberRegistration` service in a JBoss application. It uses the Arquillian framework to deploy a test archive and run the test in a container. The test archive includes the `Member`, `MemberRegistration`, and `Resources` classes, as well as configuration files for persistence and beans. The class contains a single test method, `testRegister`, which verifies that a new `Member` can be registered and persisted correctly.

```java
@RunWith(Arquillian.class)
public class MemberRegistrationIT {
    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(Member.class, MemberRegistration.class, Resources.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("META-INF/test-persistence.xml", "persistence.xml");
    }

    @Inject
    MemberRegistration memberRegistration;

    @Inject
    Logger log;

    @Test
    public void testRegister() throws Exception {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane.doe@mailinator.com");
        newMember.setPhoneNumber("2125551234");
        memberRegistration.register(newMember);
        assertNotNull(newMember.getId());
        log.info(newMember.getName() + " was persisted with id " + newMember.getId());
    }
}
```

#### src/test/java/org/jboss/as/quickstarts/kitchensink/test/RemoteMemberRegistrationIT.java
The `RemoteMemberRegistrationIT.java` file is a JUnit test class for testing the remote member registration functionality in the JBoss Kitchensink application. It uses the Jakarta JSON API to create a JSON object representing a new member and the Java HTTP Client to send a POST request to the server's member registration endpoint. The test verifies that the server responds with a status code of 200 and an empty response body, indicating successful registration.

```java
public class RemoteMemberRegistrationIT {
    private String getHTTPEndpoint() {
        return "http://" + getServerHost() + ":8080/jboss-kitchensink-web/rest/members";
    }

    private String getServerHost() {
        return System.getProperty("jboss.bind.address", "localhost");
    }

    @Test
    public void testRegister() throws Exception {
        JsonObject member = Json.createObjectBuilder()
                .add("name", "John Doe")
                .add("email", "john.doe@mailinator.com")
                .add("phoneNumber", "2125551234")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getHTTPEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(member.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());
    }
}
```
