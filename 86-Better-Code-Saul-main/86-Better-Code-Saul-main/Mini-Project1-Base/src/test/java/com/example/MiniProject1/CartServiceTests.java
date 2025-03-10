package com.example.service;
import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private UUID cartId;
    private UUID userId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testCart = new Cart(cartId, userId, new ArrayList<>());
        testProduct = new Product(UUID.randomUUID(), "Test Product", 10.0);
    }

    // 1️⃣ Test addCart()
    @Test
    void testAddCart() {
        when(cartRepository.addCart(testCart)).thenReturn(testCart);
        Cart result = cartService.addCart(testCart);
        assertNotNull(result);
        assertEquals(testCart, result);
        verify(cartRepository, times(1)).addCart(testCart);
    }

    @Test
    void testAddCart_NullCart() {
        when(cartRepository.addCart(null)).thenThrow(new IllegalArgumentException("Cart cannot be null"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.addCart(null));
        assertEquals("Cart cannot be null", exception.getMessage());
    }

    @Test
    void testAddCart_EmptyCart() {
        Cart emptyCart = new Cart();
        when(cartRepository.addCart(emptyCart)).thenReturn(emptyCart);
        Cart result = cartService.addCart(emptyCart);
        assertNotNull(result);
        verify(cartRepository, times(1)).addCart(emptyCart);
    }

    // 2️⃣ Test getCarts()
    @Test
    void testGetCarts() {
        ArrayList<Cart> carts = new ArrayList<>();
        carts.add(testCart);
        when(cartRepository.getCarts()).thenReturn(carts);
        ArrayList<Cart> result = cartService.getCarts();
        assertEquals(1, result.size());
        verify(cartRepository, times(1)).getCarts();
    }

    @Test
    void testGetCarts_EmptyList() {
        when(cartRepository.getCarts()).thenReturn(new ArrayList<>());
        ArrayList<Cart> result = cartService.getCarts();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCarts_NullResponse() {
        when(cartRepository.getCarts()).thenReturn(null);
        assertNull(cartService.getCarts());
    }

    // 3️⃣ Test getCartById()
    @Test
    void testGetCartById() {
        when(cartRepository.getCartById(cartId)).thenReturn(testCart);
        Cart result = cartService.getCartById(cartId);
        assertNotNull(result);
        assertEquals(cartId, result.getId());
    }

    @Test
    void testGetCartById_NotFound() {
        when(cartRepository.getCartById(cartId)).thenReturn(null);
        assertNull(cartService.getCartById(cartId));
    }

    @Test
    void testGetCartById_InvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(cartRepository.getCartById(invalidId)).thenReturn(null);
        assertNull(cartService.getCartById(invalidId));
    }

    // 4️⃣ Test getCartByUserId()
    @Test
    void testGetCartByUserId() {
        when(cartRepository.getCartByUserId(userId)).thenReturn(testCart);
        Cart result = cartService.getCartByUserId(userId);
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testGetCartByUserId_NotFound() {
        when(cartRepository.getCartByUserId(userId)).thenReturn(null);
        assertNull(cartService.getCartByUserId(userId));
    }

    @Test
    void testGetCartByUserId_InvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(cartRepository.getCartByUserId(invalidId)).thenReturn(null);
        assertNull(cartService.getCartByUserId(invalidId));
    }

    // 5️⃣ Test addProductToCart()
    @Test
    void testAddProductToCart() {
        doNothing().when(cartRepository).addProductToCart(userId, testProduct);
        cartService.addProductToCart(userId, testProduct);
        verify(cartRepository, times(1)).addProductToCart(userId, testProduct);
    }

    @Test
    void testAddProductToCart_NullProduct() {
        doThrow(new IllegalArgumentException("Product cannot be null"))
                .when(cartRepository).addProductToCart(userId, null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(userId, null));
        assertEquals("Product cannot be null", exception.getMessage());
    }

    @Test
    void testAddProductToCart_InvalidUser() {
        UUID invalidUserId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("User not found"))
                .when(cartRepository).addProductToCart(invalidUserId, testProduct);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(invalidUserId, testProduct));
        assertEquals("User not found", exception.getMessage());
    }

    // 6️⃣ Test deleteProductFromCart()
    @Test
    void testDeleteProductFromCart() {
        when(cartRepository.deleteProductFromCart(cartId, testProduct.getId())).thenReturn("Product removed successfully");
        String result = cartService.deleteProductFromCart(cartId, testProduct.getId());
        assertEquals("Product removed successfully", result);
    }

    @Test
    void testDeleteProductFromCart_NotFound() {
        when(cartRepository.deleteProductFromCart(cartId, testProduct.getId())).thenReturn("Product not found");
        String result = cartService.deleteProductFromCart(cartId, testProduct.getId());
        assertEquals("Product not found", result);
    }

    @Test
    void testDeleteProductFromCart_InvalidCart() {
        UUID invalidCartId = UUID.randomUUID();
        when(cartRepository.deleteProductFromCart(invalidCartId, testProduct.getId())).thenReturn("Cart not found");
        String result = cartService.deleteProductFromCart(invalidCartId, testProduct.getId());
        assertEquals("Cart not found", result);
    }

    // 7️⃣ Test deleteCartById()
    @Test
    void testDeleteCartById() {
        doNothing().when(cartRepository).deleteCartById(cartId);
        cartService.deleteCartById(cartId);
        verify(cartRepository, times(1)).deleteCartById(cartId);
    }

    @Test
    void testDeleteCartById_NotFound() {
        doThrow(new IllegalArgumentException("Cart not found"))
                .when(cartRepository).deleteCartById(cartId);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.deleteCartById(cartId));
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testDeleteCartById_InvalidId() {
        UUID invalidCartId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Invalid cart ID"))
                .when(cartRepository).deleteCartById(invalidCartId);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.deleteCartById(invalidCartId));
        assertEquals("Invalid cart ID", exception.getMessage());
    }
}
