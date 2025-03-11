package com.example.MiniProject1;


import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(UUID.randomUUID(), "Test Product", 100.0);
    }

    @Test
    void testAddProduct() {
        when(productRepository.addProduct(product)).thenReturn(product);
        Product result = productService.addProduct(product);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).addProduct(product);
    }

    @Test
    void testGetProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);
        when(productRepository.getProducts()).thenReturn(new ArrayList<>(products));

        List<Product> result = productService.getProducts();
        assertEquals(1, result.size());
        verify(productRepository, times(1)).getProducts();
    }

    @Test
    void testGetProductById() {
        UUID productId = product.getId();
        when(productRepository.getProductById(productId)).thenReturn(product);
        Product result = productService.getProductById(productId);
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository, times(1)).getProductById(productId);
    }

    @Test
    void testUpdateProduct() {
        UUID productId = product.getId();
        when(productRepository.updateProduct(productId, "Updated Name", 80.0))
                .thenReturn(new Product(productId, "Updated Name", 80.0));

        Product updatedProduct = productService.updateProduct(productId, "Updated Name", 80.0);
        assertNotNull(updatedProduct);
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals(80.0, updatedProduct.getPrice());
        verify(productRepository, times(1)).updateProduct(productId, "Updated Name", 80.0);
    }

    @Test
    void testApplyDiscount() {
        List<UUID> productIds = List.of(product.getId());
        doNothing().when(productRepository).applyDiscount(10.0, new ArrayList<>(productIds));

        productService.applyDiscount(10.0, new ArrayList<>(productIds));
        verify(productRepository, times(1)).applyDiscount(10.0, new ArrayList<>(productIds));
    }

    @Test
    void testDeleteProductById() {
        UUID productId = product.getId();
        doNothing().when(productRepository).deleteProductById(productId);

        productService.deleteProductById(productId);
        verify(productRepository, times(1)).deleteProductById(productId);
    }
}
