package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User> {

    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Autowired
    public UserService(UserRepository userRepository, CartService cartService, OrderRepository orderRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public User addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }
        if (user.getId() == null) {
            user.setId(UUID.randomUUID()); // Assign a unique ID if missing
        }

        // Check if a user with the same ID already exists
        Optional<User> existingUser = Optional.ofNullable(userRepository.getUserById(user.getId()));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with this ID already exists");
        }

        try {
            return userRepository.addUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving user: " + e.getMessage());
        }
    }

    public ArrayList<User> getUsers() {
        try {
            ArrayList<User> users = userRepository.getUsers();
            return (users != null) ? users : new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving users: " + e.getMessage());
        }
    }

    public User getUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            User user = userRepository.getUserById(userId);
            if (user == null) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

                System.out.println("User not found in JSON file: " + userId); // Debugging
                //  throw new RuntimeException("User not found with ID: " + userId);
                return null;
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving user: " + e.getMessage());
        }
    }


    public List<Order> getOrdersByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            Optional<User> user = Optional.ofNullable(userRepository.getUserById(userId));
            if (user.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            List<Order> orders = userRepository.getOrdersByUserId(userId);
            return (orders != null) ? orders : new ArrayList<>(); // Ensure a non-null list
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving orders: " + e.getMessage());
        }
    }

    public void addOrderToUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            User user = userRepository.getUserById(userId);
            if (user == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            // Get cart products
            List<Product> cartProducts = cartService.getCartByUserId(userId).getProducts();
            if (cartProducts == null || cartProducts.isEmpty()) {
                throw new RuntimeException("Cannot create an order: Cart is empty");
            }

            // Create new order
            Order newOrder = new Order(UUID.randomUUID(), userId, 0, new ArrayList<>(cartProducts));
            double totalPrice = cartProducts.stream().mapToDouble(Product::getPrice).sum();
            newOrder.setTotalPrice(totalPrice);

            // Save order
            orderRepository.addOrder(newOrder);
            userRepository.addOrderToUser(userId, newOrder);

            // Empty the cart
            emptyCart(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding order to user: " + e.getMessage());
        }
    }

    public void emptyCart(UUID userId) {
        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null) {
            System.out.println("No cart found for user: " + userId); // Debugging
            return; // Exit if no cart is found
        }

        for (Product product : cart.getProducts()) {
            cartService.deleteProductFromCart(cart.getUserId(), product.getId());
        }
    }


    public void removeOrderFromUser(UUID userId, UUID orderId) {
        if (userId == null || orderId == null) {
            throw new IllegalArgumentException("User ID and Order ID cannot be null");
        }

        try {
            // Check if user exists
            User user = userRepository.getUserById(userId);
            if (user == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            // Check if the order exists
            Order order = orderRepository.getOrderById(orderId);
            if (order == null) {
                throw new RuntimeException("Order not found with ID: " + orderId + " for user ID: " + userId);
            }

            // Remove order from the user
            userRepository.removeOrderFromUser(userId, orderId);

            // Remove order from order repository
            orderRepository.deleteOrderById(orderId);
        } catch (Exception e) {
            throw new RuntimeException("Error while removing order: " + e.getMessage());
        }
    }



    public String deleteUserById(UUID userId) {
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz" );
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            // Log the users for debugging
            System.out.println("Attempting to delete user with ID: " + userId);
            ArrayList<User> users = userRepository.getUsers();
            System.out.println("Existing users: " + users);

            // Check if user exists
            User user = getUserById(userId);
            if (user == null) {
                return "User not found";
            }

            // Remove user's orders before deleting the user
            List<Order> userOrders = user.getOrders();
            if (userOrders != null && !userOrders.isEmpty()) {
                for (Order order : userOrders) {
                    removeOrderFromUser(userId, order.getId());
                }
            }

            // Remove user's cart
            Cart cart = cartService.getCartByUserId(userId);
            if (cart != null) {
                cartService.deleteCartById(cart.getId());
            }

            // Delete user
            userRepository.deleteUserById(userId);
            return "User deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting user: " + e.getMessage());
        }
    }


}
