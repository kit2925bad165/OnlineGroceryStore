package com.grocery.store.service;

import com.grocery.store.dao.ProductDAO;
import com.grocery.store.exception.BadRequestException;
import com.grocery.store.exception.ResourceNotFoundException;
import com.grocery.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Product getProductById(int id) {
        return productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BadRequestException("Product name is required.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Product price must be a valid non-negative number.");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new BadRequestException("Stock quantity must be non-negative.");
        }
        return productDAO.save(product);
    }

    public Product updateProduct(int id, Product product) {
        getProductById(id); // Ensure product exists
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BadRequestException("Product name is required.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Product price must be a valid non-negative number.");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new BadRequestException("Stock quantity must be non-negative.");
        }
        boolean updated = productDAO.update(id, product);
        if (!updated) {
            throw new RuntimeException("Failed to update product.");
        }
        product.setId(id);
        return product;
    }

    public void deleteProduct(int id) {
        getProductById(id); // Ensure product exists
        boolean deleted = productDAO.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("Failed to delete product.");
        }
    }
}
