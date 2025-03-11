package com.example.MiniProject1;
import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class userTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId =UUID.randomUUID();
        testUser = new User(userId, "John Doe", new ArrayList<>());
    }

    @Test
    void testAddUser_Success() {
        when(userRepository.getUserById(userId)).thenReturn(null);
        when(userRepository.addUser(testUser)).thenReturn(testUser);
        User addedUser = userService.addUser(testUser);
        assertNotNull(addedUser);
        verify(userRepository).addUser(testUser);
    }

    @Test
    void testAddUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(null));
    }

    @Test
    void testAddUser_DuplicateUser() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(testUser));
    }

    @Test
    void testGetUsers_Success() {
        List<User> users = List.of(testUser);
        when(userRepository.getUsers()).thenReturn(new ArrayList<>(users));
        assertEquals(users, userService.getUsers());
    }

    @Test
    void testGetUsers_EmptyList() {
        when(userRepository.getUsers()).thenReturn(new ArrayList<>());
        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void testGetUsers_Exception() {
        when(userRepository.getUsers()).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> userService.getUsers());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        assertEquals(testUser, userService.getUserById(userId));
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.getUserById(userId)).thenReturn(null);
        assertNull(userService.getUserById(userId));
    }

    @Test
    void testGetUserById_Exception() {
        when(userRepository.getUserById(userId)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testGetOrdersByUserId_Success() {
        List<Order> orders = List.of(new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>()));
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(userRepository.getOrdersByUserId(userId)).thenReturn(orders);
        assertEquals(orders, userService.getOrdersByUserId(userId));
    }

    @Test
    void testGetOrdersByUserId_UserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.getOrdersByUserId(userId));
    }
    @Test
    void testGetOrdersByUserId_NullUserId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getOrdersByUserId(null));
    }

    @Test
    void testGetOrdersByUserId_EmptyOrderList() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(userRepository.getOrdersByUserId(userId)).thenReturn(new ArrayList<>());

        List<Order> result = userService.getOrdersByUserId(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrdersByUserId_NullOrdersReturned() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(userRepository.getOrdersByUserId(userId)).thenReturn(null);

        List<Order> result = userService.getOrdersByUserId(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Ensures null is handled and returns an empty list
    }

    @Test
    void testGetOrdersByUserId_ExceptionThrown() {
        when(userRepository.getUserById(userId)).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getOrdersByUserId(userId));
        assertEquals("Error while retrieving orders: DB error", exception.getMessage());
    }


    @Test
    void testAddOrderToUser_Success() {
        Cart cart = new Cart(userId,UUID.randomUUID() ,List.of(new Product(UUID.randomUUID(), "Product", 50.0)));
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(cartService.getCartByUserId(userId)).thenReturn(cart);
        doNothing().when(orderRepository).addOrder(any(Order.class));
        userService.addOrderToUser(userId);
        verify(orderRepository).addOrder(any(Order.class));
    }

    @Test
    void testAddOrderToUser_EmptyCart() {
        Cart cart = new Cart(userId,UUID.randomUUID() ,new ArrayList<>());
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(cartService.getCartByUserId(userId)).thenReturn(cart);
        assertThrows(RuntimeException.class, () -> userService.addOrderToUser(userId));
    }
    @Test
    void testAddOrderToUser_NullUserId() {
        assertThrows(IllegalArgumentException.class, () -> userService.addOrderToUser(null));
    }

    @Test
    void testAddOrderToUser_UserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.addOrderToUser(userId));
    }

    @Test
    void testAddOrderToUser_CartServiceReturnsNull() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(cartService.getCartByUserId(userId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.addOrderToUser(userId));
        assertEquals("Error while adding order to user: Cannot invoke \"com.example.model.Cart.getProducts()\" because the return value of \"com.example.service.CartService.getCartByUserId(java.util.UUID)\" is null", exception.getMessage());
    }


    @Test
    void testAddOrderToUser_ExceptionDuringOrderCreation() {
        Cart cart = new Cart(userId, UUID.randomUUID(), List.of(new Product(UUID.randomUUID(), "Product", 50.0)));
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(cartService.getCartByUserId(userId)).thenReturn(cart);
        doThrow(new RuntimeException("DB error")).when(orderRepository).addOrder(any(Order.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.addOrderToUser(userId));
        assertTrue(exception.getMessage().contains("Error while adding order to user: DB error"));
    }

    @Test
    void testEmptyCart_Success() {
        Cart cart = new Cart(userId, UUID.randomUUID(), List.of(
                new Product(UUID.randomUUID(), "Product1", 50.0),
                new Product(UUID.randomUUID(), "Product2", 75.0)
        ));

        when(cartService.getCartByUserId(userId)).thenReturn(cart);

        userService.emptyCart(userId);

        verify(cartService, times(2)).deleteProductFromCart(eq(cart.getUserId()), any(UUID.class));

        // Ensure cart is empty after deletion
        when(cartService.getCartByUserId(userId)).thenReturn(new Cart(userId, UUID.randomUUID(), new ArrayList<>()));
        assertTrue(cartService.getCartByUserId(userId).getProducts().isEmpty());
    }


    @Test
    void testEmptyCart_NoCartFound() {
        when(cartService.getCartByUserId(userId)).thenReturn(null);

        userService.emptyCart(userId);

        verify(cartService, never()).deleteProductFromCart(any(UUID.class), any(UUID.class));
    }

    @Test
    void testEmptyCart_EmptyCart() {
        Cart cart = new Cart(userId, UUID.randomUUID(), new ArrayList<>());

        when(cartService.getCartByUserId(userId)).thenReturn(cart);

        userService.emptyCart(userId);

        verify(cartService, never()).deleteProductFromCart(any(UUID.class), any(UUID.class));
    }
    @Test
    void testRemoveOrderFromUser_Success() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(orderRepository.getOrderById(orderId)).thenReturn(new Order(orderId, userId, 100.0, new ArrayList<>()));

        userService.removeOrderFromUser(userId, orderId);

        verify(userRepository).removeOrderFromUser(userId, orderId);
        verify(orderRepository).deleteOrderById(orderId);
    }

    @Test
    void testRemoveOrderFromUser_UserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(null);


        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.removeOrderFromUser(userId, orderId)
        );

        assertEquals("Error while removing order: User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, never()).removeOrderFromUser(any(UUID.class), any(UUID.class));
        verify(orderRepository, never()).deleteOrderById(any(UUID.class));
    }

    @Test
    void testRemoveOrderFromUser_OrderNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        when(orderRepository.getOrderById(orderId)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.removeOrderFromUser(userId, orderId)
        );

        assertEquals("Error while removing order: Order not found with ID: " + orderId + " for user ID: " + userId, exception.getMessage());
        verify(userRepository, never()).removeOrderFromUser(any(UUID.class), any(UUID.class));
        verify(orderRepository, never()).deleteOrderById(any(UUID.class));
    }

    @Test
    void testDeleteUserById_Success() {
        when(userRepository.getUserById(userId)).thenReturn(testUser);
        doNothing().when(userRepository).deleteUserById(userId);
        String result = userService.deleteUserById(userId);
        assertEquals("User deleted successfully", result);
        verify(userRepository).deleteUserById(userId);
    }

    @Test
    void testDeleteUserById_UserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(null);
        assertEquals("User not found", userService.deleteUserById(userId));
    }

    @Test
    void testDeleteUserById_Exception() {
        when(userRepository.getUserById(userId)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> userService.deleteUserById(userId));
    }
}
