package com.grocery.store.dao;

import com.grocery.store.config.DatabaseConfig;
import com.grocery.store.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDAO {

    @Autowired
    private DatabaseConfig databaseConfig;

    public Customer save(Customer customer) {
        String sql = "INSERT INTO customer (name, email, phone, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
            }

            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating customer: " + e.getMessage(), e);
        }
    }

    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT customer_id, name, email, phone, address FROM customer";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching customers: " + e.getMessage(), e);
        }

        return customers;
    }

    public Optional<Customer> findById(int id) {
        String sql = "SELECT customer_id, name, email, phone, address FROM customer WHERE customer_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address")
                    );
                    return Optional.of(customer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching customer by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT customer_id, name, email, phone, address FROM customer WHERE email = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address")
                    );
                    return Optional.of(customer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching customer by email: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Customer update(Customer customer) {
        String sql = "UPDATE customer SET name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getId());

            stmt.executeUpdate();

            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer: " + e.getMessage(), e);
        }
    }
}