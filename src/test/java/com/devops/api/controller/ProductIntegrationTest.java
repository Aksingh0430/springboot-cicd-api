package com.devops.api.controller;

import com.devops.api.dto.ProductDTO;
import com.devops.api.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")          // uses H2 + application-test.properties
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product API Integration Tests (Full Spring Context + H2)")
class ProductIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductRepository productRepository;

    private static Long createdId;

    @BeforeEach
    void cleanForFirstTest() {
        // Only wipe before the first test to give us a clean slate
        if (createdId == null) {
            productRepository.deleteAll();
        }
    }

    @Test @Order(1)
    @DisplayName("POST - Create product returns 201 with persisted data")
    void create_Returns201_WithData() throws Exception {
        ProductDTO dto = ProductDTO.builder()
                .name("Integration Test Laptop")
                .description("Full stack test product")
                .price(new BigDecimal("999.99"))
                .quantity(50)
                .category("Electronics")
                .build();

        String body = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Integration Test Laptop")))
                .andExpect(jsonPath("$.data.price", is(999.99)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        createdId = objectMapper.readTree(body).path("data").path("id").asLong();
    }

    @Test @Order(2)
    @DisplayName("GET /{id} - Fetch persisted product")
    void getById_Returns200_WithCorrectData() throws Exception {
        Assumptions.assumeTrue(createdId != null);

        mockMvc.perform(get("/api/v1/products/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Integration Test Laptop")))
                .andExpect(jsonPath("$.data.category", is("Electronics")));
    }

    @Test @Order(3)
    @DisplayName("GET / - All products returns list")
    void getAll_Returns200_WithList() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", isA(List.class)));
    }

    @Test @Order(4)
    @DisplayName("GET /category - Filter by category works")
    void getByCategory_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category", is("Electronics")));
    }

    @Test @Order(5)
    @DisplayName("GET /search - Name search returns results")
    void search_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products/search").param("name", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    @Test @Order(6)
    @DisplayName("GET /in-stock - Returns in-stock products")
    void inStock_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products/in-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test @Order(7)
    @DisplayName("PUT /{id} - Update price and quantity")
    void update_Returns200_WithNewValues() throws Exception {
        Assumptions.assumeTrue(createdId != null);

        ProductDTO update = ProductDTO.builder()
                .name("Integration Test Laptop")
                .description("Updated description")
                .price(new BigDecimal("1199.99"))
                .quantity(30)
                .category("Electronics")
                .build();

        mockMvc.perform(put("/api/v1/products/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.price", is(1199.99)))
                .andExpect(jsonPath("$.data.quantity", is(30)));
    }

    @Test @Order(8)
    @DisplayName("POST - Duplicate name returns 409 Conflict")
    void create_Returns409_WhenDuplicateName() throws Exception {
        ProductDTO dto = ProductDTO.builder()
                .name("Integration Test Laptop")   // same name as Order(1)
                .price(new BigDecimal("500.00")).quantity(5).category("Electronics")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test @Order(9)
    @DisplayName("GET /{id} - Non-existent ID returns 404")
    void getById_Returns404_ForMissingId() throws Exception {
        mockMvc.perform(get("/api/v1/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test @Order(10)
    @DisplayName("DELETE /{id} - Delete and confirm removal")
    void delete_Returns200_ThenGone() throws Exception {
        Assumptions.assumeTrue(createdId != null);

        // Delete
        mockMvc.perform(delete("/api/v1/products/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Product deleted successfully")));

        // Confirm it's gone
        mockMvc.perform(get("/api/v1/products/" + createdId))
                .andExpect(status().isNotFound());
    }

    @Test @Order(11)
    @DisplayName("GET /health - Returns UP status")
    void health_ReturnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("UP")));
    }
}
