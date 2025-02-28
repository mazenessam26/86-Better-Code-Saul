package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    public CartRepository() {
    }

    @Override
    protected String getDataPath() {
        return "path/to/carts.json";
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    public List<Cart> getCarts() {
        return findAll();
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

    public void addProductToCart(UUID cartId, Product product) {
        List<Cart> carts = findAll();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.addProducts(product);
                saveAll(new ArrayList<>(carts));
                return;
            }
        }
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        List<Cart> carts = findAll();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.removeProduct(product);
                saveAll(new ArrayList<>(carts));
                return;
            }
        }
    }

    public void deleteCartById(UUID cartId) {
        List<Cart> carts = findAll();
        carts.removeIf(cart -> cart.getId().equals(cartId));
        saveAll(new ArrayList<>(carts));
    }
}
