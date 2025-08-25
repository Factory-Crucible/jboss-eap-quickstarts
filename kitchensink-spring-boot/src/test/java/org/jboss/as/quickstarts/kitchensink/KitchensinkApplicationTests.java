package org.jboss.as.quickstarts.kitchensink;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.repository.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.test.context.ActiveProfiles;

/**
 * Comprehensive integration tests for the Kitchensink Spring Boot application.
 * Tests all layers of the application from REST API to database.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Kitchensink Application Integration Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class KitchensinkApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    private static final String API_BASE_PATH = "/api/members";
    
    // Sample test member data
    private Member createTestMember(String name, String email, String phone) {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setPhoneNumber(phone);
        return member;
    }
    
    @BeforeEach
    void setup() throws Exception {
        // Clear any existing data to ensure test isolation
        memberRepository.deleteAll();
    }

    /**
     * Test that the Spring context loads successfully
     */
    @Test
    @Order(1)
    @DisplayName("Context loads successfully")
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
        assertThat(memberRepository).isNotNull();
        assertThat(memberService).isNotNull();
    }

    /**
     * Test that the repository can find all members ordered by name
     */
    @Test
    @Order(2)
    @DisplayName("Repository finds all members ordered by name")
    void testRepositoryFindAllOrderedByName() throws Exception {
        // Initially the database should be empty
        List<Member> initialMembers = memberRepository.findAllOrderedByName();
        assertThat(initialMembers).isEmpty();
        
        // Create test members in non-alphabetical order
        memberService.register(createTestMember("Charlie Brown", "charlie@example.com", "5551112222"));
        memberService.register(createTestMember("Alice Smith", "alice@example.com", "5553334444"));
        memberService.register(createTestMember("Bob Johnson", "bob@example.com", "5555556666"));
        
        // Get members and verify ordering
        List<Member> members = memberRepository.findAllOrderedByName();
        
        assertThat(members).isNotEmpty();
        assertThat(members.size()).isEqualTo(3);
        
        // Verify ordering by name (alphabetical)
        assertThat(members.get(0).getName()).isEqualTo("Alice Smith");
        assertThat(members.get(1).getName()).isEqualTo("Bob Johnson");
        assertThat(members.get(2).getName()).isEqualTo("Charlie Brown");
    }

    /**
     * Test that the repository can find a member by email
     */
    @Test
    @Order(3)
    @DisplayName("Repository finds member by email")
    void testRepositoryFindByEmail() throws Exception {
        // Create a test member first
        Member testMember = createTestMember("John Smith", "john.smith@mailinator.com", "2125551212");
        memberService.register(testMember);
        
        // Now find the member by email
        Member foundMember = memberRepository.findByEmail("john.smith@mailinator.com");
        
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getName()).isEqualTo("John Smith");
        assertThat(foundMember.getPhoneNumber()).isEqualTo("2125551212");
    }

    /**
     * Test that the repository returns null for non-existent email
     */
    @Test
    @Order(4)
    @DisplayName("Repository returns null for non-existent email")
    void testRepositoryFindByNonExistentEmail() {
        Member member = memberRepository.findByEmail("nonexistent@example.com");
        assertThat(member).isNull();
    }

    /**
     * Test that the service can register a new member
     */
    @Test
    @Order(5)
    @DisplayName("Service registers new member successfully")
    void testServiceRegisterMember() throws Exception {
        Member newMember = new Member();
        newMember.setName("Test User");
        newMember.setEmail("test.user@mailinator.com");
        newMember.setPhoneNumber("5551234567");
        
        Member savedMember = memberService.register(newMember);
        
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("Test User");
        
        // Verify the member was actually saved
        Member foundMember = memberRepository.findByEmail("test.user@mailinator.com");
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getId()).isEqualTo(savedMember.getId());
    }

    /**
     * Test that the service throws exception for duplicate email
     */
    @Test
    @Order(6)
    @DisplayName("Service throws exception for duplicate email")
    void testServiceRegisterDuplicateEmail() throws Exception {
        // Create the initial member first
        Member initialMember = createTestMember("John Smith", "john.smith@mailinator.com", "2125551212");
        memberService.register(initialMember);
        
        // Now try to create a duplicate
        Member duplicateMember = new Member();
        duplicateMember.setName("Another John");
        duplicateMember.setEmail("john.smith@mailinator.com"); // This email already exists
        duplicateMember.setPhoneNumber("5559876543");
        
        Exception exception = assertThrows(Exception.class, () -> {
            memberService.register(duplicateMember);
        });
        
        assertThat(exception.getMessage()).contains("Email already exists");
    }

    /**
     * Test GET /api/members endpoint
     */
    @Test
    @Order(7)
    @DisplayName("REST API returns all members")
    void testGetAllMembers() throws Exception {
        // Initially there should be no members
        ResponseEntity<List<Member>> initialResponse = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {}
        );
        
        assertThat(initialResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(initialResponse.getBody()).isNotNull();
        assertThat(initialResponse.getBody()).isEmpty();
        
        // Create some test members
        memberService.register(createTestMember("John Smith", "john@example.com", "5551112222"));
        memberService.register(createTestMember("Jane Doe", "jane@example.com", "5553334444"));
        memberService.register(createTestMember("Bob Johnson", "bob@example.com", "5555556666"));
        
        // Now get all members
        ResponseEntity<List<Member>> response = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Member>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(3);
    }

    /**
     * Test GET /api/members/{id} endpoint with valid ID
     */
    @Test
    @Order(8)
    @DisplayName("REST API returns member by ID")
    void testGetMemberById() throws Exception {
        // Create a test member first
        Member testMember = createTestMember("John Smith", "john@example.com", "2125551212");
        Member savedMember = memberService.register(testMember);
        Long memberId = savedMember.getId();
        
        // Now get the member by ID
        ResponseEntity<Member> response = restTemplate.getForEntity(
                API_BASE_PATH + "/" + memberId,
                Member.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(memberId);
        assertThat(response.getBody().getName()).isEqualTo("John Smith");
    }

    /**
     * Test GET /api/members/{id} endpoint with invalid ID
     */
    @Test
    @Order(9)
    @DisplayName("REST API returns 404 for non-existent member ID")
    void testGetMemberByInvalidId() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                API_BASE_PATH + "/999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test POST /api/members endpoint with valid member
     */
    @Test
    @Order(10)
    @DisplayName("REST API creates new member successfully")
    void testCreateMember() {
        Member newMember = new Member();
        newMember.setName("API Test User");
        newMember.setEmail("api.test@mailinator.com");
        newMember.setPhoneNumber("8005551234");
        
        ResponseEntity<Member> response = restTemplate.postForEntity(
                API_BASE_PATH,
                newMember,
                Member.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("API Test User");
        
        // Verify the member was actually saved
        Member foundMember = memberRepository.findByEmail("api.test@mailinator.com");
        assertThat(foundMember).isNotNull();
    }

    /**
     * Test POST /api/members endpoint with invalid member (missing required fields)
     */
    @Test
    @Order(11)
    @DisplayName("REST API returns 400 for invalid member data")
    void testCreateInvalidMember() {
        Member invalidMember = new Member();
        // Missing required fields
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThan(0);
        // Should have validation errors for name, email, and phoneNumber
        assertTrue(response.getBody().containsKey("name") || 
                   response.getBody().containsKey("email") || 
                   response.getBody().containsKey("phoneNumber"));
    }

    /**
     * Test POST /api/members endpoint with duplicate email
     */
    @Test
    @Order(12)
    @DisplayName("REST API returns 409 for duplicate email")
    void testCreateMemberWithDuplicateEmail() throws Exception {
        // Create the initial member first
        Member initialMember = createTestMember("John Smith", "john.smith@mailinator.com", "2125551212");
        memberService.register(initialMember);
        
        // Now try to create a duplicate
        Member duplicateMember = new Member();
        duplicateMember.setName("Duplicate Email User");
        duplicateMember.setEmail("john.smith@mailinator.com"); // This email already exists
        duplicateMember.setPhoneNumber("9995551234");
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(duplicateMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertTrue(response.getBody().containsKey("email"));
        assertEquals("Email taken", response.getBody().get("email"));
    }

    /**
     * Test bean validation for invalid phone number format
     */
    @Test
    @Order(13)
    @DisplayName("Bean validation rejects invalid phone number")
    void testInvalidPhoneNumber() {
        Member invalidMember = new Member();
        invalidMember.setName("Invalid Phone");
        invalidMember.setEmail("invalid.phone@mailinator.com");
        invalidMember.setPhoneNumber("123"); // Too short
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertTrue(response.getBody().containsKey("phoneNumber"));
    }

    /**
     * Test bean validation for invalid email format
     */
    @Test
    @Order(14)
    @DisplayName("Bean validation rejects invalid email")
    void testInvalidEmail() {
        Member invalidMember = new Member();
        invalidMember.setName("Invalid Email");
        invalidMember.setEmail("not-an-email"); // Invalid email format
        invalidMember.setPhoneNumber("5551234567");
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertTrue(response.getBody().containsKey("email"));
    }

    /**
     * Test bean validation for invalid name (contains numbers)
     */
    @Test
    @Order(15)
    @DisplayName("Bean validation rejects name with numbers")
    void testInvalidName() {
        Member invalidMember = new Member();
        invalidMember.setName("Name123"); // Contains numbers, which is not allowed
        invalidMember.setEmail("invalid.name@mailinator.com");
        invalidMember.setPhoneNumber("5551234567");
        
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                API_BASE_PATH,
                HttpMethod.POST,
                new HttpEntity<>(invalidMember),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertTrue(response.getBody().containsKey("name"));
    }
}
