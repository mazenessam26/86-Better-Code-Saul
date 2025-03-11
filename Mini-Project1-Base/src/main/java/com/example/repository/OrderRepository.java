package com.example.repository;

import com.example.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {

    @Value("${spring.application.orderDataPath}")
    private String filepath;

    public OrderRepository() {
    }

    @Override
    protected String getDataPath() {
        return filepath; // Path to the orders JSON file
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    public void addOrder(Order order) {
        save(order);
    }

    public ArrayList<Order> getOrders() {
        return findAll();
    }

    public Order getOrderById(UUID orderId) {
        return findAll().stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public void deleteOrderById(UUID orderId) {
        ArrayList<Order> orders = findAll();
        orders.removeIf(order -> order.getId().equals(orderId));
        overrideData(orders);
    }
}
