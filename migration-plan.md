# Migration Plan: JBoss Kitchensink to Spring Boot 3

## Overview

This document outlines the plan for migrating the core application components from the JBoss 'kitchensink' application to a new Spring Boot 3 application. The migration will preserve all existing functionality while updating the implementation to use Spring Boot conventions and best practices.

## Table of Contents

1. [Project Setup](#1-project-setup)
2. [Domain Model Migration](#2-domain-model-migration)
3. [Data Access Layer Migration](#3-data-access-layer-migration)
4. [Business Logic Migration](#4-business-logic-migration)
5. [REST API Migration](#5-rest-api-migration)
6. [Application Properties Migration](#6-application-properties-migration)
7. [Exception Handling Migration](#7-exception-handling-migration)
8. [Bean Validation Migration](#8-bean-validation-migration)
9. [Testing Strategy](#9-testing-strategy)
10. [Deployment Considerations](#10-deployment-considerations)

## 1. Project Setup

### Tasks:

1. **Create a new Spring Boot project** using Spring Initializr (https://start.spring.io/) with the following specifications:
   - Project: Maven
   - Language: Java
   - Spring Boot: 3.4.1 (latest stable version)
   - Packaging: JAR
   - Java Version: 17 (minimum required for Spring Boot 3)

2. **Add required dependencies**:
   - `spring-boot-starter-web`: For RESTful web services
   - `spring-boot-starter-data-jpa`: For JPA support
   - `spring-boot-starter-validation`: For bean validation
   - `h2`: For development database (can be replaced with MySQL/PostgreSQL for production)
   - `lombok`: For reducing boilerplate code (optional)
   - `springdoc-openapi-starter-webmvc-ui`: For API documentation (optional)

3. **Set up project structure** following Spring Boot conventions:
   ```
   src/main/java/com/example/kitchensink/
   ├── KitchensinkApplication.java
   ├── config/
   ├── controller/
   ├── exception/
   ├── model/
   ├── repository/
   ├── service/
   └── util/
   ```

4. **Create a GitHub repository** for version control and collaboration.

## 2. Domain Model Migration

### Tasks:

1. **Analyze existing JPA entities** in the JBoss application:
   - Identify all entities (e.g., `Member`)
   - Document their relationships, constraints, and validations

2. **Create corresponding entity classes** in the Spring Boot application:
   - Migrate the `Member` entity first
   - Ensure all Jakarta EE annotations are correctly applied
   - Update any JBoss-specific annotations to Spring Boot equivalents

3. **Example Migration - Member Entity**:

   **JBoss Version**:
   ```java
   @Entity
   @XmlRootElement
   @Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
   public class Member implements Serializable {
       @Id
       @GeneratedValue
       private Long id;
       
       // Other fields and methods
   }
   ```

   **Spring Boot Version**:
   ```java
   @Entity
   @Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
   public class Member implements Serializable {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       // Other fields and methods
   }
   ```

4. **Consider using Lombok** for reducing boilerplate code:
   ```java
   @Entity
   @Data // Lombok annotation for getters, setters, equals, hashCode, toString
   @NoArgsConstructor // Lombok annotation for no-args constructor
   @Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
   public class Member implements Serializable {
       // Fields only, no getters/setters needed
   }
   ```

## 3. Data Access Layer Migration

### Tasks:

1. **Analyze existing data access code** in the JBoss application:
   - Identify repository/DAO classes
   - Document query methods and custom implementation

2. **Create Spring Data JPA repositories**:
   - Create an interface extending `JpaRepository` for each entity
   - Migrate custom query methods using Spring Data JPA's method naming conventions or `@Query` annotations

3. **Example Migration - Member Repository**:

   **JBoss Version**:
   ```java
   @ApplicationScoped
   public class MemberRepository {
       @PersistenceContext
       private EntityManager em;

       public Member findById(Long id) {
           return em.find(Member.class, id);
       }
       
       public Member findByEmail(String email) {
           // Custom query implementation
       }
       
       // Other methods
   }
   ```

   **Spring Boot Version**:
   ```java
   @Repository
   public interface MemberRepository extends JpaRepository<Member, Long> {
       Optional<Member> findByEmail(String email);
       
       // Other query methods
   }
   ```

4. **Implement custom repository methods** if needed:
   ```java
   @Repository
   public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
       // Standard query methods
   }

   public interface CustomMemberRepository {
       // Custom method declarations
   }

   @Component
   public class CustomMemberRepositoryImpl implements CustomMemberRepository {
       @PersistenceContext
       private EntityManager em;
       
       // Custom method implementations
   }
   ```

## 4. Business Logic Migration

### Tasks:

1. **Analyze existing service classes** in the JBoss application:
   - Identify service methods and their functionality
   - Document transaction requirements and event handling

2. **Create corresponding service classes** in the Spring Boot application:
   - Implement service interfaces and their implementations
   - Apply appropriate annotations (`@Service`, `@Transactional`)
   - Inject required repositories using constructor injection

3. **Example Migration - Member Service**:

   **JBoss Version**:
   ```java
   @Stateless
   public class MemberRegistration {
       @Inject
       private Logger log;
       
       @Inject
       private EntityManager em;
       
       @Inject
       private Event<Member> memberEventSrc;
       
       public void register(Member member) throws Exception {
           log.info("Registering " + member.getName());
           em.persist(member);
           memberEventSrc.fire(member);
       }
   }
   ```

   **Spring Boot Version**:
   ```java
   @Service
   @Transactional
   public class MemberService {
       private final MemberRepository memberRepository;
       private final ApplicationEventPublisher eventPublisher;
       private final Logger logger = LoggerFactory.getLogger(MemberService.class);
       
       public MemberService(MemberRepository memberRepository, ApplicationEventPublisher eventPublisher) {
           this.memberRepository = memberRepository;
           this.eventPublisher = eventPublisher;
       }
       
       public Member register(Member member) {
           logger.info("Registering {}", member.getName());
           Member savedMember = memberRepository.save(member);
           eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
           return savedMember;
       }
       
       // Other service methods
   }
   ```

4. **Implement event handling** using Spring's event system:
   ```java
   // Event class
   public class MemberRegisteredEvent {
       private final Member member;
       
       public MemberRegisteredEvent(Member member) {
           this.member = member;
       }
       
       public Member getMember() {
           return member;
       }
   }
   
   // Event listener
   @Component
   public class MemberEventListener {
       private final Logger logger = LoggerFactory.getLogger(MemberEventListener.class);
       
       @EventListener
       public void handleMemberRegisteredEvent(MemberRegisteredEvent event) {
           logger.info("Member registered: {}", event.getMember().getName());
           // Additional event handling logic
       }
   }
   ```

## 5. REST API Migration

### Tasks:

1. **Analyze existing REST endpoints** in the JBoss application:
   - Identify all REST resources and their methods
   - Document request/response formats and status codes

2. **Create corresponding REST controllers** in the Spring Boot application:
   - Implement controller classes with appropriate annotations
   - Map endpoints to service methods
   - Apply proper HTTP method annotations and path mappings

3. **Example Migration - Member REST Resource**:

   **JBoss Version**:
   ```java
   @Path("/members")
   @RequestScoped
   public class MemberResourceRESTService {
       @Inject
       private MemberRepository repository;
       
       @GET
       @Produces(MediaType.APPLICATION_JSON)
       public List<Member> listAllMembers() {
           return repository.findAllOrderedByName();
       }
       
       @GET
       @Path("/{id:[0-9][0-9]*}")
       @Produces(MediaType.APPLICATION_JSON)
       public Member lookupMemberById(@PathParam("id") long id) {
           Member member = repository.findById(id);
           if (member == null) {
               throw new WebApplicationException(Response.Status.NOT_FOUND);
           }
           return member;
       }
       
       // Other methods
   }
   ```

   **Spring Boot Version**:
   ```java
   @RestController
   @RequestMapping("/api/members")
   public class MemberController {
       private final MemberService memberService;
       
       public MemberController(MemberService memberService) {
           this.memberService = memberService;
       }
       
       @GetMapping
       public List<Member> getAllMembers() {
           return memberService.findAll();
       }
       
       @GetMapping("/{id}")
       public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
           return memberService.findById(id)
               .map(ResponseEntity::ok)
               .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
       }
       
       @PostMapping
       @ResponseStatus(HttpStatus.CREATED)
       public Member createMember(@Valid @RequestBody Member member) {
           return memberService.register(member);
       }
       
       // Other endpoints
   }
   ```

4. **Consider implementing DTOs** (Data Transfer Objects) to separate API models from entity models:
   ```java
   // DTO class
   @Data
   public class MemberDTO {
       private Long id;
       private String name;
       private String email;
       private String phoneNumber;
       
       // No JPA annotations here
   }
   
   // Controller using DTO
   @RestController
   @RequestMapping("/api/members")
   public class MemberController {
       private final MemberService memberService;
       private final MemberMapper memberMapper;
       
       // Methods using DTOs
       @GetMapping("/{id}")
       public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
           return memberService.findById(id)
               .map(memberMapper::toDto)
               .map(ResponseEntity::ok)
               .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
       }
   }
   ```

## 6. Application Properties Migration

### Tasks:

1. **Analyze existing configuration** in the JBoss application:
   - Identify all configuration properties and their values
   - Document database connection settings, logging configuration, etc.

2. **Create `application.properties` or `application.yml`** in the Spring Boot application:
   - Configure database connection
   - Set up logging
   - Configure other application-specific properties

3. **Example Migration - Application Properties**:

   **JBoss Configuration**:
   ```xml
   <!-- Various XML configuration files -->
   ```

   **Spring Boot Configuration** (`application.yml`):
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:testdb
       driver-class-name: org.h2.Driver
       username: sa
       password: password
     jpa:
       database-platform: org.hibernate.dialect.H2Dialect
       hibernate:
         ddl-auto: update
       show-sql: true
     
   logging:
     level:
       root: INFO
       com.example.kitchensink: DEBUG
       org.hibernate: INFO
   
   server:
     port: 8080
     servlet:
       context-path: /kitchensink
   ```

4. **Implement profile-specific configuration** for different environments:
   ```yaml
   # application-dev.yml
   spring:
     datasource:
       url: jdbc:h2:mem:testdb
   
   # application-prod.yml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/kitchensink
       driver-class-name: com.mysql.cj.jdbc.Driver
       username: ${DB_USERNAME}
       password: ${DB_PASSWORD}
   ```

## 7. Exception Handling Migration

### Tasks:

1. **Analyze existing exception handling** in the JBoss application:
   - Identify custom exceptions and their usage
   - Document error response formats

2. **Implement global exception handling** in the Spring Boot application:
   - Create custom exception classes
   - Implement a global exception handler using `@ControllerAdvice`
   - Define consistent error response formats

3. **Example Migration - Exception Handling**:

   **Custom Exceptions**:
   ```java
   public class ResourceNotFoundException extends RuntimeException {
       public ResourceNotFoundException(String message) {
           super(message);
       }
   }
   
   public class ValidationException extends RuntimeException {
       private final Map<String, String> errors;
       
       public ValidationException(Map<String, String> errors) {
           super("Validation failed");
           this.errors = errors;
       }
       
       public Map<String, String> getErrors() {
           return errors;
       }
   }
   ```

   **Global Exception Handler**:
   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(ResourceNotFoundException.class)
       public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
           ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
           return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
       }
       
       @ExceptionHandler(ValidationException.class)
       public ResponseEntity<ValidationErrorResponse> handleValidationException(ValidationException ex) {
           ValidationErrorResponse error = new ValidationErrorResponse("VALIDATION_FAILED", "Validation failed", ex.getErrors());
           return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
       }
       
       @ExceptionHandler(MethodArgumentNotValidException.class)
       public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
           Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
               .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
           
           ValidationErrorResponse error = new ValidationErrorResponse("VALIDATION_FAILED", "Validation failed", errors);
           return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
       }
       
       // Other exception handlers
   }
   ```

   **Error Response Classes**:
   ```java
   @Data
   public class ErrorResponse {
       private final String code;
       private final String message;
       private final LocalDateTime timestamp = LocalDateTime.now();
   }
   
   @Data
   @EqualsAndHashCode(callSuper = true)
   public class ValidationErrorResponse extends ErrorResponse {
       private final Map<String, String> errors;
   }
   ```

## 8. Bean Validation Migration

### Tasks:

1. **Analyze existing bean validation** in the JBoss application:
   - Identify validation annotations and custom validators
   - Document validation groups and messages

2. **Implement bean validation** in the Spring Boot application:
   - Apply validation annotations to entity classes and DTOs
   - Configure validation messages
   - Implement custom validators if needed

3. **Example Migration - Bean Validation**:

   **Entity Validation**:
   ```java
   @Entity
   @Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
   public class Member implements Serializable {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @NotNull
       @Size(min = 1, max = 25, message = "Name must be between 1 and 25 characters")
       @Pattern(regexp = "[^0-9]*", message = "Name must not contain numbers")
       private String name;
       
       @NotNull
       @NotEmpty
       @Email(message = "Email must be valid")
       private String email;
       
       @NotNull
       @Size(min = 10, max = 12, message = "Phone number must be between 10 and 12 digits")
       @Digits(fraction = 0, integer = 12, message = "Phone number must contain only digits")
       @Column(name = "phone_number")
       private String phoneNumber;
       
       // Getters and setters
   }
   ```

   **Custom Validator**:
   ```java
   @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
   @Retention(RetentionPolicy.RUNTIME)
   @Constraint(validatedBy = UniqueEmailValidator.class)
   public @interface UniqueEmail {
       String message() default "Email already exists";
       Class<?>[] groups() default {};
       Class<? extends Payload>[] payload() default {};
   }
   
   public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
       private final MemberRepository memberRepository;
       
       public UniqueEmailValidator(MemberRepository memberRepository) {
           this.memberRepository = memberRepository;
       }
       
       @Override
       public boolean isValid(String email, ConstraintValidatorContext context) {
           if (email == null) {
               return true;
           }
           return !memberRepository.existsByEmail(email);
       }
   }
   ```

   **Validation in Controller**:
   ```java
   @RestController
   @RequestMapping("/api/members")
   public class MemberController {
       // Other methods
       
       @PostMapping
       @ResponseStatus(HttpStatus.CREATED)
       public Member createMember(@Valid @RequestBody Member member) {
           return memberService.register(member);
       }
   }
   ```

## 9. Testing Strategy

### Tasks:

1. **Analyze existing tests** in the JBoss application:
   - Identify unit tests, integration tests, and end-to-end tests
   - Document test coverage and testing frameworks used

2. **Implement a comprehensive testing strategy** for the Spring Boot application:
   - Unit tests for services and utilities
   - Integration tests for repositories and controllers
   - End-to-end tests for critical workflows

3. **Example Test Implementation**:

   **Unit Test for Service**:
   ```java
   @ExtendWith(MockitoExtension.class)
   public class MemberServiceTest {
       @Mock
       private MemberRepository memberRepository;
       
       @Mock
       private ApplicationEventPublisher eventPublisher;
       
       @InjectMocks
       private MemberService memberService;
       
       @Test
       void testRegisterMember() {
           // Arrange
           Member member = new Member();
           member.setName("John Doe");
           member.setEmail("john@example.com");
           member.setPhoneNumber("1234567890");
           
           when(memberRepository.save(any(Member.class))).thenReturn(member);
           
           // Act
           Member result = memberService.register(member);
           
           // Assert
           assertNotNull(result);
           assertEquals("John Doe", result.getName());
           verify(memberRepository).save(member);
           verify(eventPublisher).publishEvent(any(MemberRegisteredEvent.class));
       }
       
       // Other tests
   }
   ```

   **Integration Test for Repository**:
   ```java
   @DataJpaTest
   public class MemberRepositoryTest {
       @Autowired
       private MemberRepository memberRepository;
       
       @Test
       void testFindByEmail() {
           // Arrange
           Member member = new Member();
           member.setName("John Doe");
           member.setEmail("john@example.com");
           member.setPhoneNumber("1234567890");
           memberRepository.save(member);
           
           // Act
           Optional<Member> result = memberRepository.findByEmail("john@example.com");
           
           // Assert
           assertTrue(result.isPresent());
           assertEquals("John Doe", result.get().getName());
       }
       
       // Other tests
   }
   ```

   **Integration Test for Controller**:
   ```java
   @WebMvcTest(MemberController.class)
   public class MemberControllerTest {
       @Autowired
       private MockMvc mockMvc;
       
       @MockBean
       private MemberService memberService;
       
       @Test
       void testGetAllMembers() throws Exception {
           // Arrange
           List<Member> members = Arrays.asList(
               createMember(1L, "John Doe", "john@example.com", "1234567890"),
               createMember(2L, "Jane Doe", "jane@example.com", "0987654321")
           );
           
           when(memberService.findAll()).thenReturn(members);
           
           // Act & Assert
           mockMvc.perform(get("/api/members")
                   .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].name", is("John Doe")))
               .andExpect(jsonPath("$[1].name", is("Jane Doe")));
       }
       
       // Helper methods and other tests
   }
   ```

## 10. Deployment Considerations

### Tasks:

1. **Containerization**:
   - Create a `Dockerfile` for building a container image
   - Configure Docker Compose for local development
   - Prepare Kubernetes manifests for production deployment

2. **CI/CD Pipeline**:
   - Set up GitHub Actions or Jenkins for continuous integration
   - Implement automated testing and deployment
   - Configure environment-specific deployment pipelines

3. **Monitoring and Observability**:
   - Implement health checks using Spring Boot Actuator
   - Configure metrics collection using Micrometer
   - Set up distributed tracing using Spring Cloud Sleuth and Zipkin

4. **Documentation**:
   - Generate API documentation using SpringDoc OpenAPI
   - Create comprehensive README and usage guides
   - Document deployment procedures and troubleshooting steps

## Timeline and Resources

### Phase 1: Setup and Domain Model (Week 1)
- Project setup and configuration
- Domain model migration
- Data access layer migration

### Phase 2: Business Logic and API (Week 2)
- Business logic migration
- REST API migration
- Exception handling and validation

### Phase 3: Testing and Deployment (Week 3)
- Comprehensive testing
- Containerization and deployment setup
- Documentation and knowledge transfer

### Resources Required:
- 2-3 Java developers with Spring Boot experience
- 1 DevOps engineer for CI/CD and deployment
- Access to existing JBoss application codebase and documentation
- Development, testing, and production environments

## Conclusion

This migration plan provides a comprehensive roadmap for migrating the JBoss 'kitchensink' application to a modern Spring Boot 3 application. By following this plan, we will ensure that all existing functionality is preserved while leveraging the benefits of Spring Boot's convention-over-configuration approach and modern Java development practices.

The migration will result in a more maintainable, testable, and scalable application that can be easily extended and deployed in modern cloud environments.
