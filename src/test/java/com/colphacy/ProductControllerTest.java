package com.colphacy;

import com.colphacy.dto.product.ProductAdminListViewDTO;
import com.colphacy.payload.response.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPaginatedProductsAdmin_WithInvalidJwt() throws Exception {
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
    void testGetPaginatedProductsAdmin_WithInvalidEndpoint() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        mockMvc.perform(MockMvcRequestBuilders.get("/product")
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
    void testGetPaginatedProductsAdmin_WithInvalidParams() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("offset", "-1")
                        .param("limit", "-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testGetPaginatedProductsAdmin_WithDefaultParams() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PageResponse pageResponse = new ObjectMapper().readValue(response, PageResponse.class);

        assertEquals(0, pageResponse.getOffset());
        assertEquals(10, pageResponse.getLimit());
        assertEquals(5, pageResponse.getItems().size());
    }

    @Test
    void testGetPaginatedProductsAdmin_WithNoRecords() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("keyword", "KTPM"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PageResponse pageResponse = new ObjectMapper().readValue(response, PageResponse.class);

        assertEquals(10, pageResponse.getLimit());
        assertEquals(0, pageResponse.getItems().size());
    }

    @Test
    void testGetPaginatedProductsAdmin_WithInvalidCategoryId() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token


        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("offset", "1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testGetPaginatedProductsAdmin_WithCategoryIdNotFound() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("categoryId", "5"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testGetPaginatedProductsAdmin_WithCategoryIdNoRecords() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("categoryId", "4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PageResponse pageResponse = new ObjectMapper().readValue(response, PageResponse.class);

        assertEquals(0, pageResponse.getItems().size());
    }

    @Test
    void testGetPaginatedProductsAdmin_WithMatchingKeywordButCategoryIdNoRecords() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("categoryId", "4")
                        .param("keyword", "Thuốc")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PageResponse pageResponse = new ObjectMapper().readValue(response, PageResponse.class);

        assertEquals(0, pageResponse.getItems().size());
    }

    @Test
    void testGetPaginatedProductsAdmin_FilteredByCategoryIdAndKeyword() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("categoryId", "3")
                        .param("keyword", "Thuốc")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PageResponse pageResponse = new ObjectMapper().readValue(response, PageResponse.class);

        assertEquals(3, pageResponse.getItems().size());
    }

    @Test
    void testGetPaginatedProductsAdmin_FilteredByCategoryIdAndKeywordWithOffset1Limit2() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiYXV0aG9yaXR5IjoiQURNSU4iLCJpYXQiOjE3MDE4MTkwNzUsImV4cCI6MTcwMTkzMDY3NX0.2ltN1liKCOGfepmAdV5ufwIQ-PtXO3ZJM058rE2DUCuKguL6smF0nkFj_rc1shRQ8vkJ6lxnhPAz34du9JZGQA"; // replace with an invalid JWT token

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("categoryId", "3")
                        .param("keyword", "Thuốc")
                        .param("offset", "1")
                        .param("limit", "2")
                        .param("sortBy", "id")
                        .param("orderBy", "ASC")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PageResponse<ProductAdminListViewDTO> pageResponse = new ObjectMapper().readValue(response, new TypeReference<PageResponse<ProductAdminListViewDTO>>() {
        });

        assertEquals(2, pageResponse.getItems().size());
        assertEquals(10, pageResponse.getItems().get(0).getId());
        assertEquals(1, pageResponse.getOffset());
        assertEquals(2, pageResponse.getLimit());
    }

}
