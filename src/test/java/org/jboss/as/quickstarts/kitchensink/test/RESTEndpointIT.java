package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.rest.JaxRsActivator;
import org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RESTEndpointIT {

    private static final Logger log = Logger.getLogger(RESTEndpointIT.class.getName());

    @Inject
    private MemberResourceRESTService memberResourceRESTService;

    @ArquillianResource
    private URL deploymentUrl;

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Member.class, MemberResourceRESTService.class, JaxRsActivator.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-ds.xml", "test-ds.xml");
    }

    @Test
    public void testRegisterMember() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Member member = new Member();
            member.setName("John Doe");
            member.setEmail("john@example.com");
            member.setPhoneNumber("1234567890");

            Response response = client.target(deploymentUrl + "rest/members")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(member, MediaType.APPLICATION_JSON));

            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull(response.getHeaderString("Location"));
        } finally {
            client.close();
        }
    }

    @Test
    public void testGetMember() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(deploymentUrl + "rest/members/1")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Member member = response.readEntity(Member.class);
            assertNotNull(member);
            assertEquals("John Doe", member.getName());
        } finally {
            client.close();
        }
    }

    @Test
    public void testGetNonExistentMember() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(deploymentUrl + "rest/members/999")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        } finally {
            client.close();
        }
    }

    @Test
    public void testListAllMembers() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(deploymentUrl + "rest/members")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Member[] members = response.readEntity(Member[].class);
            assertNotNull(members);
            assert(members.length > 0);
        } finally {
            client.close();
        }
    }

    @Test
    public void testUpdateMember() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Member updatedMember = new Member();
            updatedMember.setName("Jane Doe");
            updatedMember.setEmail("jane@example.com");
            updatedMember.setPhoneNumber("9876543210");

            Response response = client.target(deploymentUrl + "rest/members/1")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(updatedMember, MediaType.APPLICATION_JSON));

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Member returnedMember = response.readEntity(Member.class);
            assertEquals("Jane Doe", returnedMember.getName());
        } finally {
            client.close();
        }
    }

    @Test
    public void testDeleteMember() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(deploymentUrl + "rest/members/1")
                    .request()
                    .delete();

            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

            // Verify deletion
            response = client.target(deploymentUrl + "rest/members/1")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        } finally {
            client.close();
        }
    }
}