package org.jboss.as.quickstarts.kitchensink.test;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.extension.RestAssuredExtension;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(RestAssuredExtension.class)
public class MemberResourceRESTServiceIT {

    @BeforeAll
    public static void setup() {
        // Set base URI for REST Assured
        RestAssured.baseURI = "http://localhost:8080/kitchensink-quickstart";
    }

    @Test
    public void serverIsResponsive() {
        given()
            .when()
                .get("/rest/members")
            .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
}
import com.fasterxml.jackson.databind.ObjectMapper;

    @Test
    public void testGetAllMembers() {
        given()
            .when()
                .get("/rest/members")
            .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateMember() {
        String newMember = "{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"phoneNumber\":\"1234567890\"}";

        given()
            .contentType("application/json")
            .body(newMember)
        .when()
            .post("/rest/members")
        .then()
            .statusCode(201)
            .header("Location", not(emptyOrNullString()));
    }

    @Test
    public void testUpdateMember() {
        // First, create a member
        String newMember = "{\"name\":\"Jane Doe\",\"email\":\"jane@example.com\",\"phoneNumber\":\"0987654321\"}";
        String location = given()
            .contentType("application/json")
            .body(newMember)
        .when()
            .post("/rest/members")
        .then()
            .statusCode(201)
            .extract().header("Location");

        // Now update the member
        String updatedMember = "{\"name\":\"Jane Smith\",\"email\":\"janesmith@example.com\",\"phoneNumber\":\"1122334455\"}";
        given()
            .contentType("application/json")
            .body(updatedMember)
        .when()
            .put(location)
        .then()
            .statusCode(200);
    }

    @Test
    public void testDeleteMember() {
        // First, create a member
        String newMember = "{\"name\":\"Bob Smith\",\"email\":\"bob@example.com\",\"phoneNumber\":\"5566778899\"}";
        String location = given()
            .contentType("application/json")
            .body(newMember)
        .when()
            .post("/rest/members")
        .then()
            .statusCode(201)
            .extract().header("Location");

        // Now delete the member
        given()
        .when()
            .delete(location)
        .then()
            .statusCode(204);

        // Verify the member is deleted
        given()
        .when()
            .get(location)
        .then()
            .statusCode(404);
    }

    @Test
    public void testInvalidMemberCreation() {
        String invalidMember = "{\"name\":\"\",\"email\":\"invalid-email\",\"phoneNumber\":\"123\"}";

        given()
            .contentType("application/json")
            .body(invalidMember)
        .when()
            .post("/rest/members")
        .then()
            .statusCode(400);
    }
