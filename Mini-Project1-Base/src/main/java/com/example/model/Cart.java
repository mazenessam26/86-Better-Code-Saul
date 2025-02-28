package com.example.model;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class Cart {

    private UUID id;
    private UUID userId;
    private List<Product> products = new ArrayList<>();


    public Cart() {

    }
    public Cart(UUID id,UUID userId,ArrayList<Product> products) {
        this.id = id;
        this.userId = userId;
        this.products = products;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProduct(ArrayList<Product> products) {
        this.products = products;
    }
    public void addProducts(Product product) {
        this.products.add(product);
    }
    public void removeProduct(Product product) {
        this.products.remove(product);
    }
    public String toString(){
        return "Cart id: "+this.id+" User id: "+this.userId+" Products: "+this.products;
    }
}
