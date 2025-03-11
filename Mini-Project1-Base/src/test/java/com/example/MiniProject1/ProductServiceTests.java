package com.example.MiniProject1;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleProduct = new Product(UUID.randomUUID(), "Test Product", 100.0);
    }

    @Test
    void testAddProductSuccess() {
        when(productRepository.addProduct(sampleProduct)).thenReturn(sampleProduct);

        Product result = productService.addProduct(sampleProduct);

        assertNotNull(result);
        assertEquals(sampleProduct, result);
    }

    @Test
    void testAddProductWithNull() {
        when(productRepository.addProduct(null)).thenThrow(new IllegalArgumentException("Product cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(null));
    }

    @Test
    void testAddProductFailure() {
        when(productRepository.addProduct(sampleProduct)).thenReturn(null);

        Product result = productService.addProduct(sampleProduct);

        assertNull(result);
    }

    @Test
    void testGetProductsNotEmpty() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(sampleProduct);
        when(productRepository.getProducts()).thenReturn(products);

        ArrayList<Product> result = productService.getProducts();

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetProductsEmptyList() {
        when(productRepository.getProducts()).thenReturn(new ArrayList<>());

        ArrayList<Product> result = productService.getProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductsNull() {
        when(productRepository.getProducts()).thenReturn(null);

        ArrayList<Product> result = productService.getProducts();

        assertNull(result);
    }

    @Test
    void testGetProductByIdSuccess() {
        UUID productId = sampleProduct.getId();
        when(productRepository.getProductById(productId)).thenReturn(sampleProduct);

        Product result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(sampleProduct, result);
    }

    @Test
    void testGetProductByIdNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.getProductById(productId)).thenReturn(null);

        Product result = productService.getProductById(productId);

        assertNull(result);
    }

    @Test
    void testGetProductByIdWithNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProductById(null));
    }

    @Test
    void testUpdateProductSuccess() {
        UUID productId = sampleProduct.getId();
        Product updatedProduct = new Product(productId, "Updated Product", 150.0);

        when(productRepository.updateProduct(productId, "Updated Product", 150.0)).thenReturn(updatedProduct);

        Product result = productService.updateProduct(productId, "Updated Product", 150.0);

        assertEquals("Updated Product", result.getName());
        assertEquals(150.0, result.getPrice());
    }

    @Test
    void testUpdateProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.updateProduct(productId, "Updated Product", 150.0)).thenReturn(null);

        Product result = productService.updateProduct(productId, "Updated Product", 150.0);

        assertNull(result);
    }

    @Test
    void testUpdateProductWithInvalidPrice() {
        UUID productId = sampleProduct.getId();
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(productId, "Updated Product", -10.0));
    }

    @Test
    void testApplyDiscountValid() {
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(sampleProduct.getId());

        doNothing().when(productRepository).applyDiscount(20.0, productIds);

        productService.applyDiscount(20.0, productIds);

        verify(productRepository, times(1)).applyDiscount(20.0, productIds);
    }

    @Test
    void testApplyDiscountWithNegativeValue() {
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(sampleProduct.getId());

        assertThrows(IllegalArgumentException.class, () -> productService.applyDiscount(-10.0, productIds));
    }

    @Test
    void testApplyDiscountWithEmptyList() {
        ArrayList<UUID> productIds = new ArrayList<>();

        doNothing().when(productRepository).applyDiscount(20.0, productIds);

        productService.applyDiscount(20.0, productIds);

        verify(productRepository, times(1)).applyDiscount(20.0, productIds);
    }

    @Test
    void testDeleteProductByIdSuccess() {
        UUID productId = sampleProduct.getId();
        doNothing().when(productRepository).deleteProductById(productId);

        productService.deleteProductById(productId);

        verify(productRepository, times(1)).deleteProductById(productId);
    }

    @Test
    void testDeleteProductByIdNotFound() {
        UUID productId = UUID.randomUUID();
        doNothing().when(productRepository).deleteProductById(productId);

        productService.deleteProductById(productId);

        verify(productRepository, times(1)).deleteProductById(productId);
    }

    @Test
    void testDeleteProductByIdWithNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProductById(null));
    }

}
