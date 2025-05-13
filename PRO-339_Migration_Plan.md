# Migration Plan: JBoss Kitchensink to Spring Boot

## Overview

This document outlines the plan for migrating the core application components from the JBoss 'kitchensink' application to a new Spring Boot application (Phase 2 of the migration). The kitchensink application demonstrates Jakarta EE technologies including CDI, JPA, JSF, and Bean Validation, and our goal is to preserve all functionalities while updating them to use Spring Boot conventions.

## Table of Contents

1. [Spring Boot Architecture Best Practices](#spring-boot-architecture-best-practices)
2. [Migration Components](#migration-components)
   - [Domain Model](#domain-model)
   - [Data Access Layer](#data-access-layer)
   - [Business Logic](#business-logic)
   - [REST API](#rest-api)
   - [Application Properties](#application-properties)
   - [Exception Handling](#exception-handling)
   - [Bean Validation](#bean-validation)
3. [Testing Strategy](#testing-strategy)
4. [Deployment Considerations](#deployment-considerations)

## Spring Boot Architecture Best Practices

Before detailing the migration steps, we'll establish the target architecture for our Spring Boot application following industry best practices:

### Layered Architecture

The Spring Boot application will follow a standard layered architecture:

1. **Controller Layer**: Handles HTTP requests and responses, maps to REST endpoints
2. **Service Layer**: Contains business logic and orchestrates operations
3. **Repository Layer**: Interfaces with the database through Spring Data JPA
4. **Model Layer**: Contains domain entities and DTOs (Data Transfer Objects)

### Package Structure

We'll adopt a package-by-feature approach with the following structure:

```
com.factory.kitchensink
├── config/                  # Configuration classes
├── controller/              # REST controllers
├── dto/                     # Data Transfer Objects
├── exception/               # Custom exceptions and handlers
├── mapper/                  # Object mappers (entity <-> DTO)
├── model/                   # Domain entities
├── repository/              # Spring Data JPA repositories
├── service/                 # Service interfaces
│   └── impl/                # Service implementations
└── KitchensinkApplication.java  # Main application class
```

### Best Practices

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **DTO Pattern**: Use DTOs to decouple the API from internal domain models
3. **Interface-based Services**: Define service interfaces with implementations for better testability
4. **Repository Pattern**: Use Spring Data JPA repositories for data access
5. **Centralized Exception Handling**: Implement global exception handlers
6. **Bean Validation**: Use Jakarta Bean Validation annotations on DTOs and entities

## Migration Components

### Domain Model

The domain model migration involves converting JPA entities from Jakarta EE to Spring Boot.

#### Steps:

1. **Identify Existing Entities**: The kitchensink application has a `Member` entity that needs to be migrated.

2. **Update Import Statements**: Change Jakarta EE imports to their Spring Boot equivalents:
   ```java
   // From
   import jakarta.persistence.*;
   import jakarta.validation.constraints.*;
   
   // To
   import javax.persistence.*;
   import javax.validation.constraints.*;
   ```
   
   > Note: Spring Boot 3.x uses Jakarta EE imports (`jakarta.*`), while Spring Boot 2.x uses Java EE imports (`javax.*`). Since we're targeting the latest Spring Boot, we'll use Jakarta EE imports.

3. **Create Entity Classes**: Recreate the `Member` entity with appropriate annotations:
   ```java
   @Entity
   @Table(name = "member")
   public class Member {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @NotNull
       @Size(min = 1, max = 25)
       @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
       private String name;
       
       @NotNull
       @NotEmpty
       @Email
       private String email;
       
       @NotNull
       @Size(min = 10, max = 12)
       @Digits(fraction = 0, integer = 12)
       private String phoneNumber;
       
       // Getters and setters
   }
   ```

4. **Create DTOs**: Add DTOs to separate API representation from the domain model:
   ```java
   public class MemberDTO {
       private Long id;
       private String name;
       private String email;
       private String phoneNumber;
       
       // Getters and setters
   }
   
   public class CreateMemberDTO {
       @NotNull
       @Size(min = 1, max = 25)
       @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
       private String name;
       
       @NotNull
       @NotEmpty
       @Email
       private String email;
       
       @NotNull
       @Size(min = 10, max = 12)
       @Digits(fraction = 0, integer = 12)
       private String phoneNumber;
       
       // Getters and setters
   }
   ```

5. **Create Mappers**: Implement mappers to convert between entities and DTOs:
   ```java
   @Component
   public class MemberMapper {
       public MemberDTO toDTO(Member member) {
           MemberDTO dto = new MemberDTO();
           dto.setId(member.getId());
           dto.setName(member.getName());
           dto.setEmail(member.getEmail());
           dto.setPhoneNumber(member.getPhoneNumber());
           return dto;
       }
       
       public Member toEntity(CreateMemberDTO dto) {
           Member member = new Member();
           member.setName(dto.getName());
           member.setEmail(dto.getEmail());
           member.setPhoneNumber(dto.getPhoneNumber());
           return member;
       }
   }
   ```

### Data Access Layer

The data access layer migration involves converting the JPA repositories to Spring Data JPA.

#### Steps:

1. **Identify Existing Data Access Code**: The kitchensink application uses JPA with CDI for data access.

2. **Create Spring Data JPA Repositories**: Implement repositories for each entity:
   ```java
   @Repository
   public interface MemberRepository extends JpaRepository<Member, Long> {
       Optional<Member> findByEmail(String email);
       List<Member> findByNameContainingOrderByName(String name);
   }
   ```

3. **Configure JPA Properties**: Set up the JPA configuration in `application.properties` or `application.yml`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:kitchensink
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.jpa.hibernate.ddl-auto=update
   spring.h2.console.enabled=true
   ```

4. **Migrate Custom Queries**: Convert any custom JPQL queries to Spring Data JPA:
   ```java
   // From (JPA with CDI)
   @PersistenceContext
   private EntityManager em;
   
   public Member findByEmail(String email) {
       CriteriaBuilder cb = em.getCriteriaBuilder();
       CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
       Root<Member> member = criteria.from(Member.class);
       criteria.select(member).where(cb.equal(member.get("email"), email));
       return em.createQuery(criteria).getSingleResult();
   }
   
   // To (Spring Data JPA)
   // Simply use the repository method:
   // memberRepository.findByEmail(email);
   ```

### Business Logic

The business logic migration involves converting CDI services to Spring services.

#### Steps:

1. **Identify Existing Services**: The kitchensink application has a `MemberRegistration` service that needs to be migrated.

2. **Create Service Interfaces**: Define interfaces for each service:
   ```java
   public interface MemberService {
       MemberDTO register(CreateMemberDTO memberDTO);
       List<MemberDTO> findAllMembers();
       MemberDTO findById(Long id);
       MemberDTO findByEmail(String email);
   }
   ```

3. **Implement Services**: Create implementations for each service interface:
   ```java
   @Service
   @Transactional
   public class MemberServiceImpl implements MemberService {
       private final MemberRepository memberRepository;
       private final MemberMapper memberMapper;
       
       public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper) {
           this.memberRepository = memberRepository;
           this.memberMapper = memberMapper;
       }
       
       @Override
       public MemberDTO register(CreateMemberDTO memberDTO) {
           // Check if email already exists
           if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
               throw new EmailAlreadyExistsException("Email already exists: " + memberDTO.getEmail());
           }
           
           // Convert DTO to entity
           Member member = memberMapper.toEntity(memberDTO);
           
           // Save entity
           member = memberRepository.save(member);
           
           // Return DTO
           return memberMapper.toDTO(member);
       }
       
       @Override
       public List<MemberDTO> findAllMembers() {
           return memberRepository.findAll().stream()
                   .map(memberMapper::toDTO)
                   .collect(Collectors.toList());
       }
       
       @Override
       public MemberDTO findById(Long id) {
           return memberRepository.findById(id)
                   .map(memberMapper::toDTO)
                   .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
       }
       
       @Override
       public MemberDTO findByEmail(String email) {
           return memberRepository.findByEmail(email)
                   .map(memberMapper::toDTO)
                   .orElseThrow(() -> new MemberNotFoundException("Member not found with email: " + email));
       }
   }
   ```

4. **Convert CDI Events**: Replace CDI events with Spring events:
   ```java
   // From (CDI)
   @Inject
   private Event<Member> memberEventSrc;
   
   public void register(Member member) throws Exception {
       em.persist(member);
       memberEventSrc.fire(member);
   }
   
   // To (Spring)
   @Autowired
   private ApplicationEventPublisher eventPublisher;
   
   public MemberDTO register(CreateMemberDTO memberDTO) {
       // ... (existing code)
       
       // Publish event
       eventPublisher.publishEvent(new MemberRegisteredEvent(member));
       
       return memberMapper.toDTO(member);
   }
   ```

### REST API

The REST API migration involves converting JAX-RS endpoints to Spring MVC controllers.

#### Steps:

1. **Identify Existing REST Endpoints**: The kitchensink application has a `MemberResourceRESTService` that needs to be migrated.

2. **Create REST Controllers**: Implement Spring MVC controllers for each resource:
   ```java
   @RestController
   @RequestMapping("/api/members")
   public class MemberController {
       private final MemberService memberService;
       
       public MemberController(MemberService memberService) {
           this.memberService = memberService;
       }
       
       @GetMapping
       public ResponseEntity<List<MemberDTO>> getAllMembers() {
           List<MemberDTO> members = memberService.findAllMembers();
           return ResponseEntity.ok(members);
       }
       
       @GetMapping("/{id}")
       public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
           MemberDTO member = memberService.findById(id);
           return ResponseEntity.ok(member);
       }
       
       @PostMapping
       public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody CreateMemberDTO memberDTO) {
           MemberDTO createdMember = memberService.register(memberDTO);
           return ResponseEntity
                   .created(URI.create("/api/members/" + createdMember.getId()))
                   .body(createdMember);
       }
   }
   ```

3. **Convert JAX-RS Annotations**: Replace JAX-RS annotations with Spring MVC annotations:
   ```java
   // From (JAX-RS)
   @Path("/members")
   @RequestScoped
   public class MemberResourceRESTService {
       @GET
       @Produces(MediaType.APPLICATION_JSON)
       public List<Member> listAllMembers() {
           // ...
       }
       
       @GET
       @Path("/{id:[0-9][0-9]*}")
       @Produces(MediaType.APPLICATION_JSON)
       public Member lookupMemberById(@PathParam("id") long id) {
           // ...
       }
       
       @POST
       @Consumes(MediaType.APPLICATION_JSON)
       @Produces(MediaType.APPLICATION_JSON)
       public Response createMember(Member member) {
           // ...
       }
   }
   
   // To (Spring MVC)
   @RestController
   @RequestMapping("/api/members")
   public class MemberController {
       @GetMapping
       public List<MemberDTO> getAllMembers() {
           // ...
       }
       
       @GetMapping("/{id}")
       public MemberDTO getMemberById(@PathVariable Long id) {
           // ...
       }
       
       @PostMapping
       public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody CreateMemberDTO memberDTO) {
           // ...
       }
   }
   ```

### Application Properties

The application properties migration involves converting JBoss configuration to Spring Boot properties.

#### Steps:

1. **Identify Existing Configuration**: The kitchensink application uses `persistence.xml` and other configuration files.

2. **Create Spring Boot Properties**: Implement `application.properties` or `application.yml`:
   ```properties
   # Server configuration
   server.port=8080
   server.servlet.context-path=/kitchensink
   
   # Database configuration
   spring.datasource.url=jdbc:h2:mem:kitchensink
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.jpa.hibernate.ddl-auto=update
   spring.h2.console.enabled=true
   
   # Logging configuration
   logging.level.root=INFO
   logging.level.com.factory.kitchensink=DEBUG
   
   # Actuator configuration
   management.endpoints.web.exposure.include=health,info,metrics
   ```

3. **Configure Profiles**: Set up development, testing, and production profiles:
   ```properties
   # application-dev.properties
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   
   # application-prod.properties
   spring.jpa.show-sql=false
   server.port=80
   ```

4. **Migrate Data Import**: Convert `import.sql` to Spring Boot's `data.sql`:
   ```sql
   -- data.sql
   INSERT INTO member (name, email, phone_number) VALUES ('John Smith', 'john@example.com', '2125551212');
   INSERT INTO member (name, email, phone_number) VALUES ('Jane Doe', 'jane@example.com', '2125552323');
   ```

### Exception Handling

The exception handling migration involves implementing a global exception handler for the Spring Boot application.

#### Steps:

1. **Identify Existing Exception Handling**: The kitchensink application uses JAX-RS exception mappers.

2. **Create Custom Exceptions**: Define application-specific exceptions:
   ```java
   public class MemberNotFoundException extends RuntimeException {
       public MemberNotFoundException(String message) {
           super(message);
       }
   }
   
   public class EmailAlreadyExistsException extends RuntimeException {
       public EmailAlreadyExistsException(String message) {
           super(message);
       }
   }
   ```

3. **Implement Global Exception Handler**: Create a controller advice to handle exceptions:
   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(MemberNotFoundException.class)
       public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException ex) {
           ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
           return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
       }
       
       @ExceptionHandler(EmailAlreadyExistsException.class)
       public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
           ErrorResponse error = new ErrorResponse("CONFLICT", ex.getMessage());
           return new ResponseEntity<>(error, HttpStatus.CONFLICT);
       }
       
       @ExceptionHandler(MethodArgumentNotValidException.class)
       public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
           Map<String, String> errors = new HashMap<>();
           ex.getBindingResult().getFieldErrors().forEach(error -> 
               errors.put(error.getField(), error.getDefaultMessage()));
           
           ErrorResponse error = new ErrorResponse("VALIDATION_FAILED", "Validation failed", errors);
           return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
       }
       
       @ExceptionHandler(Exception.class)
       public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
           ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
           return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
   
   public class ErrorResponse {
       private String code;
       private String message;
       private Map<String, String> errors;
       
       // Constructors, getters, and setters
   }
   ```

### Bean Validation

The bean validation migration involves converting Jakarta Bean Validation to Spring Boot validation.

#### Steps:

1. **Identify Existing Validation**: The kitchensink application uses Bean Validation annotations on entities.

2. **Add Validation Dependencies**: Ensure the Spring Boot validation starter is included:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
   </dependency>
   ```

3. **Apply Validation Annotations**: Add validation annotations to DTOs and entities:
   ```java
   public class CreateMemberDTO {
       @NotNull
       @Size(min = 1, max = 25)
       @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
       private String name;
       
       @NotNull
       @NotEmpty
       @Email
       private String email;
       
       @NotNull
       @Size(min = 10, max = 12)
       @Digits(fraction = 0, integer = 12)
       private String phoneNumber;
       
       // Getters and setters
   }
   ```

4. **Enable Validation in Controllers**: Add `@Valid` annotation to controller methods:
   ```java
   @PostMapping
   public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody CreateMemberDTO memberDTO) {
       MemberDTO createdMember = memberService.register(memberDTO);
       return ResponseEntity
               .created(URI.create("/api/members/" + createdMember.getId()))
               .body(createdMember);
   }
   ```

5. **Implement Custom Validators**: Create custom validators if needed:
   ```java
   @Constraint(validatedBy = UniqueEmailValidator.class)
   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
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
           return email != null && !memberRepository.findByEmail(email).isPresent();
       }
   }
   ```

## Testing Strategy

1. **Unit Tests**: Write unit tests for services and controllers using JUnit and Mockito.
2. **Integration Tests**: Implement integration tests using Spring Boot Test and TestContainers.
3. **API Tests**: Test REST endpoints using RestAssured or TestRestTemplate.
4. **Validation Tests**: Verify validation rules work as expected.
5. **Migration Verification**: Compare behavior of old and new applications to ensure functionality is preserved.

## Deployment Considerations

1. **Build Process**: Use Maven or Gradle to build the Spring Boot application.
2. **Containerization**: Create a Dockerfile to containerize the application.
3. **Configuration**: Externalize configuration for different environments.
4. **Monitoring**: Set up Spring Boot Actuator for monitoring and health checks.
5. **Documentation**: Generate API documentation using SpringDoc OpenAPI.

---

This migration plan provides a comprehensive approach to migrating the JBoss kitchensink application to Spring Boot while preserving all functionalities and adhering to Spring Boot best practices.
