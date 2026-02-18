package com.devops.api.service;

import com.devops.api.dto.ProductDTO;
import com.devops.api.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product createProduct(ProductDTO productDTO);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);

    List<Product> getProductsByCategory(String category);

    List<Product> searchProductsByName(String name);

    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> getInStockProducts();

    List<Product> getOutOfStockProducts();
}
