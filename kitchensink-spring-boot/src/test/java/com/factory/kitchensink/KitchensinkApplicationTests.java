package com.factory.kitchensink;

import com.factory.kitchensink.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class KitchensinkApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listAll_returnsSeedData() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    @Test
    void create_validMember_returns201() throws Exception {
        String json = "{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"phoneNumber\":\"1112223333\"}";
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/members/")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void create_invalidEmail_returns400() throws Exception {
        String json = "{\"name\":\"Bob\",\"email\":\"not-an-email\",\"phoneNumber\":\"1112223333\"}";
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")));
    }

    @Test
    void create_duplicateEmail_returns409() throws Exception {
        String json = "{\"name\":\"John\",\"email\":\"john.smith@example.com\",\"phoneNumber\":\"1112223333\"}";
        mockMvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void get_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/api/members/99999"))
                .andExpect(status().isNotFound());
    }
}
