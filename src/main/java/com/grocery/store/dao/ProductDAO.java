package com.grocery.store.dao;

import com.grocery.store.config.DatabaseConfig;
import com.grocery.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAO {

    @Autowired
    private DatabaseConfig databaseConfig;

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, name, description, price, stock, category FROM products";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock"),
                        rs.getString("category")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching products: " + e.getMessage(), e);
        }
        return products;
    }

    public Optional<Product> findById(int id) {
        String sql = "SELECT product_id, name, description, price, stock, category FROM products WHERE product_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock"),
                            rs.getString("category")
                    );
                    return Optional.of(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Product save(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setString(5, product.getCategory());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving product: " + e.getMessage(), e);
        }
    }

    public boolean update(int id, Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ?, category = ? WHERE product_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setString(5, product.getCategory());
            stmt.setInt(6, id);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product: " + e.getMessage(), e);
        }
    }

    public void updateStock(Connection conn, int productId, int newStock) throws SQLException {
        String sql = "UPDATE products SET stock = ? WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
}
