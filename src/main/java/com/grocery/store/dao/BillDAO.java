package com.grocery.store.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.grocery.store.config.DatabaseConfig;
import com.grocery.store.model.Bill;

@Repository
public class BillDAO {

    @Autowired
    private DatabaseConfig databaseConfig;

    // Create bill inside an existing transaction
    public void createBillInTransaction(
            Connection conn,
            int orderId,
            BigDecimal totalAmount) throws SQLException {

        BigDecimal subtotal = totalAmount;
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal deliveryCharge = BigDecimal.ZERO;

        String sql =
                "INSERT INTO bill " +
                "(order_id, subtotal, tax, delivery_charge, total_amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setBigDecimal(2, subtotal);
            stmt.setBigDecimal(3, tax);
            stmt.setBigDecimal(4, deliveryCharge);
            stmt.setBigDecimal(5, totalAmount);

            stmt.executeUpdate();
        }
    }

    // Get bill using order ID
    public Optional<Bill> getBillByOrderId(int orderId) {

        String sql =
                "SELECT bill_id, order_id, subtotal, tax, " +
                "delivery_charge, total_amount, bill_date " +
                "FROM bill " +
                "WHERE order_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Timestamp timestamp =
                            rs.getTimestamp("bill_date");

                    LocalDateTime billDate =
                            timestamp != null
                                    ? timestamp.toLocalDateTime()
                                    : LocalDateTime.now();

                    Bill bill = new Bill(
                            rs.getInt("bill_id"),
                            rs.getInt("order_id"),
                            rs.getBigDecimal("subtotal"),
                            rs.getBigDecimal("tax"),
                            rs.getBigDecimal("delivery_charge"),
                            rs.getBigDecimal("total_amount"),
                            billDate
                    );

                    return Optional.of(bill);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error fetching bill for order id: "
                            + e.getMessage(),
                    e
            );
        }

        return Optional.empty();
    }
}