package com.devops.api.controller;

import com.devops.api.dto.ProductDTO;
import com.devops.api.exception.ResourceNotFoundException;
import com.devops.api.model.Product;
import com.devops.api.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Tests (MockMvc)")
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private ProductService productService;
    @Autowired private ObjectMapper objectMapper;

    private Product mockProduct;
    private ProductDTO mockProductDTO;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(1L).name("Test Product").description("Test Desc")
                .price(new BigDecimal("99.99")).quantity(10).category("Electronics")
                .build();

        mockProductDTO = ProductDTO.builder()
                .name("Test Product").description("Test Desc")
                .price(new BigDecimal("99.99")).quantity(10).category("Electronics")
                .build();
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /products - 201 when valid")
    void createProduct_Returns201_WhenValid() throws Exception {
        when(productService.createProduct(any())).thenReturn(mockProduct);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProductDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Test Product")))
                .andExpect(jsonPath("$.data.price", is(99.99)));

        verify(productService).createProduct(any());
    }

    @Test
    @DisplayName("POST /products - 400 when name is blank")
    void createProduct_Returns400_WhenNameBlank() throws Exception {
        mockProductDTO.setName("");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProductDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("POST /products - 400 when price is zero")
    void createProduct_Returns400_WhenPriceZero() throws Exception {
        mockProductDTO.setPrice(BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProductDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /products/{id} - 200 when found")
    void getById_Returns200_WhenFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(mockProduct);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.category", is("Electronics")));
    }

    @Test
    @DisplayName("GET /products/{id} - 404 when not found")
    void getById_Returns404_WhenNotFound() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("GET /products - 200 with list")
    void getAll_Returns200_WithList() throws Exception {
        Product p2 = Product.builder().id(2L).name("P2")
                .price(new BigDecimal("49.99")).quantity(5).category("Books").build();

        when(productService.getAllProducts()).thenReturn(List.of(mockProduct, p2));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Test Product")))
                .andExpect(jsonPath("$.data[1].name", is("P2")));
    }

    @Test
    @DisplayName("GET /products/category/{cat} - 200 with filtered list")
    void getByCategory_Returns200() throws Exception {
        when(productService.getProductsByCategory("Electronics")).thenReturn(List.of(mockProduct));

        mockMvc.perform(get("/api/v1/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].category", is("Electronics")));
    }

    @Test
    @DisplayName("GET /products/search?name= - 200 with results")
    void search_Returns200() throws Exception {
        when(productService.searchProductsByName("Test")).thenReturn(List.of(mockProduct));

        mockMvc.perform(get("/api/v1/products/search").param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @DisplayName("GET /products/in-stock - 200 with in-stock products")
    void getInStock_Returns200() throws Exception {
        when(productService.getInStockProducts()).thenReturn(List.of(mockProduct));

        mockMvc.perform(get("/api/v1/products/in-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /products/{id} - 200 when updated")
    void update_Returns200_WhenValid() throws Exception {
        Product updated = Product.builder().id(1L).name("Updated")
                .price(new BigDecimal("149.99")).quantity(20).category("Electronics").build();

        when(productService.updateProduct(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Product updated successfully")));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /products/{id} - 200 when deleted")
    void delete_Returns200_WhenDeleted() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Product deleted successfully")));

        verify(productService).deleteProduct(1L);
    }
}
