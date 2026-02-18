package com.devops.api.service;

import com.devops.api.dto.ProductDTO;
import com.devops.api.exception.DuplicateResourceException;
import com.devops.api.exception.ResourceNotFoundException;
import com.devops.api.model.Product;
import com.devops.api.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product mockProduct;
    private ProductDTO mockProductDTO;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("Electronics")
                .build();

        mockProductDTO = ProductDTO.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("Electronics")
                .build();
    }

    @Test
    @DisplayName("createProduct - Should create and return product")
    void createProduct_ShouldReturnProduct_WhenValidData() {
        when(productRepository.existsByNameIgnoreCase("Test Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        Product result = productService.createProduct(mockProductDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualByComparingTo("99.99");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct - Should throw DuplicateResourceException when name exists")
    void createProduct_ShouldThrowDuplicate_WhenNameExists() {
        when(productRepository.existsByNameIgnoreCase("Test Product")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(mockProductDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Test Product");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("getProductById - Should return product when found")
    void getProductById_ShouldReturnProduct_WhenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("getProductById - Should throw ResourceNotFoundException when not found")
    void getProductById_ShouldThrowNotFound_WhenNotExists() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getAllProducts - Should return all products")
    void getAllProducts_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(mockProduct));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("getProductsByCategory - Should return filtered products")
    void getProductsByCategory_ShouldReturnFiltered() {
        when(productRepository.findByCategory("Electronics")).thenReturn(List.of(mockProduct));

        List<Product> result = productService.getProductsByCategory("Electronics");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("getProductsByPriceRange - Should throw when minPrice > maxPrice")
    void getProductsByPriceRange_ShouldThrow_WhenInvalidRange() {
        assertThatThrownBy(() -> productService.getProductsByPriceRange(
                new BigDecimal("200.00"), new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minPrice");
    }

    @Test
    @DisplayName("getProductsByPriceRange - Should return products in valid range")
    void getProductsByPriceRange_ShouldReturnProducts_WhenValidRange() {
        when(productRepository.findByPriceRange(
                new BigDecimal("50.00"), new BigDecimal("150.00")))
                .thenReturn(List.of(mockProduct));

        List<Product> result = productService.getProductsByPriceRange(
                new BigDecimal("50.00"), new BigDecimal("150.00"));

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("updateProduct - Should return updated product")
    void updateProduct_ShouldReturnUpdated_WhenValidData() {
        ProductDTO updateDTO = ProductDTO.builder()
                .name("Updated Product").price(new BigDecimal("149.99"))
                .quantity(20).category("Electronics").build();

        Product updated = Product.builder()
                .id(1L).name("Updated Product").price(new BigDecimal("149.99"))
                .quantity(20).category("Electronics").build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.existsByNameIgnoreCase("Updated Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        Product result = productService.updateProduct(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getPrice()).isEqualByComparingTo("149.99");
    }

    @Test
    @DisplayName("updateProduct - Should throw when product not found")
    void updateProduct_ShouldThrowNotFound_WhenProductMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, mockProductDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProduct - Should delete when product exists")
    void deleteProduct_ShouldDelete_WhenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertThatCode(() -> productService.deleteProduct(1L)).doesNotThrowAnyException();
        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct - Should throw when product not found")
    void deleteProduct_ShouldThrowNotFound_WhenMissing() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).deleteById(any());
    }
}
