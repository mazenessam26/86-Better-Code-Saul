package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    @Value("${spring.application.cartDataPath}")
    private String filepath;

    public CartRepository() {
    }

    @Override
    protected String getDataPath() {
        return filepath;
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    public Cart addCart(Cart cart) {
        List<Cart> carts = findAll();
        carts.add(cart);
        saveAll((ArrayList<Cart>) carts);
        return cart;
    }

    public ArrayList<Cart> getCarts() {
        return new ArrayList<>(findAll());
    }

    public Cart getCartById(UUID cartId) {
        return findAll().stream()
                .filter(cart -> cart.getId().equals(cartId))
                .findFirst()
                .orElse(null);
    }

    public Cart getCartByUserId(UUID userId) {
        return findAll().stream()
                .filter(cart -> cart.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public void addProductToCart(UUID userId, Product product) {
        List<Cart> carts = findAll();
        Cart userCart = null;

        // Find the user's cart
        for (Cart cart : carts) {
            if (cart.getUserId() != null && cart.getUserId().equals(userId)) {
                userCart = cart;
                break;
            }
        }

        // If no cart is found, create a new one
        if (userCart == null) {
            userCart = new Cart(UUID.randomUUID(),userId, new ArrayList<>());
            carts.add(userCart);
        }

        // Add the product to the cart
        if (userCart.getProducts() == null) {
            userCart.setProducts(new ArrayList<>());
        }
        userCart.getProducts().add(product);

        // Save the updated cart list
        saveAll(new ArrayList<>(carts));
    }


    public String deleteProductFromCart(UUID cartId, UUID productId) {
        List<Cart> carts = findAll();

        // Check if there are any carts available
        if (carts.isEmpty()) {
            return "Cart is empty";
        }

        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                if (cart.getProducts().isEmpty()) {
                    return "Cart is not empty.";
                }

                boolean removed = cart.getProducts().removeIf(p -> p.getId().equals(productId));
                if (!removed) {
                    return "Product with ID " + productId + " not found in the cart.";
                }

                saveAll((ArrayList<Cart>) carts);
                return "Product deleted from cart";
            }
        }

        return "Product deleted from cart";
    }



    public void deleteCartById(UUID cartId) {
        List<Cart> carts = findAll();
        carts.removeIf(cart -> cart.getId().equals(cartId));
        saveAll((ArrayList<Cart>) carts);
    }
}
