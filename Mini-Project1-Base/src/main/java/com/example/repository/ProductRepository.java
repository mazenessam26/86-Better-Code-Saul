package com.example.repository;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.example.model.Product;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {

    @Value("${spring.application.productDataPath}")
    private String filepath;

    public ProductRepository() {
    }

    @Override
    protected String getDataPath() {
        return filepath;
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public Product addProduct(Product product) {
        save(product);
        return product;
    }

    public ArrayList<Product> getProducts() {
        return findAll();
    }

    public Product getProductById(UUID productId) {
        return findAll().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> products = findAll();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                saveAll(products);
                return product;
            }
        }
        return null;
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        ArrayList<Product> products = findAll();
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - (discount / 100));
                product.setPrice(newPrice);
            }
        }
        saveAll(products);
    }

    public void deleteProductById(UUID productId) {
        ArrayList<Product> products = findAll();
        products.removeIf(product -> product.getId().equals(productId));
        saveAll(products);
    }
}
