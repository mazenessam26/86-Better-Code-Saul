package com.example.service;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User> {

    private final UserRepository userRepository;
    private final CartService cartService;

    @Autowired
    public UserService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUserById(UUID userId) {
        return userRepository.getUserById(userId);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return userRepository.getOrdersByUserId(userId);
    }

    public void addOrderToUser(UUID userId) {
        User user = userRepository.getUserById(userId);
        if (user != null) {
            Order newOrder = new Order(UUID.randomUUID(), userId, 0, new ArrayList<>(cartService.getCartByUserId(userId).getProducts()));
            double totalPrice = newOrder.getProducts().stream().mapToDouble(p -> p.getPrice()).sum();
            newOrder.setTotalPrice(totalPrice);
            userRepository.addOrderToUser(userId, newOrder);
            emptyCart(userId);
        }
    }

    public void emptyCart(UUID userId) {
        cartService.deleteCartById(cartService.getCartByUserId(userId).getId());
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        userRepository.removeOrderFromUser(userId, orderId);
    }

    public void deleteUserById(UUID userId) {
        userRepository.deleteUserById(userId);
    }
}
