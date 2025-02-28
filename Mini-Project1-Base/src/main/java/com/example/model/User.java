package com.example.model;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class User {

    private UUID id;
    private String  name;
    private List<Order> orders = new ArrayList<>();


    public User() {

    }
    public User(UUID id,String name,ArrayList<Order> orders) {
        this.id = id;
        this.name = name;
        this.orders = orders;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
    public void addOrders(Order order) {
        this.orders.add(order);
    }
    public void removeOrder(Order order) {
        this.orders.remove(order);
    }
    public String toString(){
        return "User id: "+this.id+" Name: "+this.name+" Orders: "+this.orders;
    }
}
