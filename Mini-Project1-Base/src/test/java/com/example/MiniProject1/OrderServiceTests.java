package com.example.MiniProject1;


import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID userId = UUID.randomUUID();  // A valid user ID
        List<Product> products = new ArrayList<>();  // Empty product list
        orderId = UUID.randomUUID();
        testOrder = new Order(orderId, userId, 50.0, products);
    }

    // 1️⃣ Test addOrder()
    @Test
    void testAddOrder() {
        doNothing().when(orderRepository).addOrder(testOrder);
        orderService.addOrder(testOrder);
        verify(orderRepository, times(1)).addOrder(testOrder);
    }

    @Test
    void testAddOrder_NullOrder() {
        doThrow(new IllegalArgumentException("Order cannot be null"))
                .when(orderRepository).addOrder(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(null));
        assertEquals("Order cannot be null", exception.getMessage());
    }

    @Test
    void testAddOrder_EmptyOrder() {
        Order emptyOrder = new Order();
        doNothing().when(orderRepository).addOrder(emptyOrder);
        orderService.addOrder(emptyOrder);
        verify(orderRepository, times(1)).addOrder(emptyOrder);
    }

    // 2️⃣ Test getOrders()
    @Test
    void testGetOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(testOrder);
        when(orderRepository.getOrders()).thenReturn(orders);
        ArrayList<Order> result = orderService.getOrders();
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).getOrders();
    }

    @Test
    void testGetOrders_EmptyList() {
        when(orderRepository.getOrders()).thenReturn(new ArrayList<>());
        ArrayList<Order> result = orderService.getOrders();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrders_NullResponse() {
        when(orderRepository.getOrders()).thenReturn(null);
        assertNull(orderService.getOrders());
    }

    // 3️⃣ Test getOrderById()
    @Test
    void testGetOrderById() {
        when(orderRepository.getOrderById(orderId)).thenReturn(testOrder);
        Order result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.getOrderById(orderId)).thenReturn(null);
        assertNull(orderService.getOrderById(orderId));
    }

    @Test
    void testGetOrderById_InvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(orderRepository.getOrderById(invalidId)).thenReturn(null);
        assertNull(orderService.getOrderById(invalidId));
    }

    // 4️⃣ Test deleteOrderById()
    @Test
    void testDeleteOrderById() {
        doNothing().when(orderRepository).deleteOrderById(orderId);
        orderService.deleteOrderById(orderId);
        verify(orderRepository, times(1)).deleteOrderById(orderId);
    }

    @Test
    void testDeleteOrderById_NotFound() {
        doThrow(new IllegalArgumentException("Order not found"))
                .when(orderRepository).deleteOrderById(orderId);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrderById(orderId));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testDeleteOrderById_InvalidId() {
        UUID invalidOrderId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Invalid order ID"))
                .when(orderRepository).deleteOrderById(invalidOrderId);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrderById(invalidOrderId));
        assertEquals("Invalid order ID", exception.getMessage());
    }
}