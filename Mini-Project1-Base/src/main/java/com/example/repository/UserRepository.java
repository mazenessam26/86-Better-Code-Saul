package com.example.repository;

import com.example.model.User;
import com.example.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User> {

    public UserRepository() {
    }

    @Override
    protected String getDataPath() {
        return "path/to/users.json";
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public ArrayList<User> getUsers() {
        return findAll();
    }

    public User getUserById(UUID userId) {
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public User addUser(User user) {
        save(user);
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        User user = getUserById(userId);
        return user != null ? user.getOrders() : new ArrayList<>();
    }

    public void addOrderToUser(UUID userId, Order order) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.addOrders(order);
                saveAll(new ArrayList<>(users));
                return;
            }
        }
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().removeIf(order -> order.getId().equals(orderId));
                saveAll(new ArrayList<>(users));
                return;
            }
        }
    }

    public void deleteUserById(UUID userId) {
        List<User> users = findAll();
        users.removeIf(user -> user.getId().equals(userId));
        saveAll(new ArrayList<>(users));
    }
}
