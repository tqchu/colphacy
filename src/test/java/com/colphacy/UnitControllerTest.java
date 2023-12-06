package com.colphacy;

import com.colphacy.repository.UnitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UnitRepository unitRepository;

    @AfterEach
    public void cleanup() {
        unitRepository.deleteUnitByName("Tá");
    }

    @Test
    void testCreateNewUnit_WithInvalidJwt() throws Exception {
        String jwtToken = "invalid-jwt-token";

        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("keyword", "test")
                        .param("categoryId", "1")
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("sortBy", "name")
                        .param("order", "asc"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testCreateNewUnit_WithInvalidEndpoint() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        mockMvc.perform(MockMvcRequestBuilders.get("/units")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("keyword", "test")
                        .param("categoryId", "1")
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("sortBy", "name")
                        .param("order", "asc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testCreateNewUnit_WithMissingName() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        mockMvc.perform(MockMvcRequestBuilders.post("/api/units")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateNewUnit_WithNameBlank() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        String requestBody = "{\"name\":\"\"}"; // request body with blank name

        mockMvc.perform(MockMvcRequestBuilders.post("/api/units")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateNewUnit_WithNameOver255Characters() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        String requestBody = "{\"name\":\"sfdssssssfgsdffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\"}"; // request body with blank name

        mockMvc.perform(MockMvcRequestBuilders.post("/api/units")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateNewUnit_DuplicateUnit() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token


        String requestBody = "{\"name\":\"Hộp\"}"; // request body with blank name

        mockMvc.perform(MockMvcRequestBuilders.post("/api/units")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateNewUnit_Successfully() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token


        String requestBody = "{\"name\":\"Tá\"}"; // request body with blank name

        mockMvc.perform(MockMvcRequestBuilders.post("/api/units")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
