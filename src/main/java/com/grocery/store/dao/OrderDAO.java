package com.grocery.store.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.grocery.store.config.DatabaseConfig;
import com.grocery.store.model.Cart;
import com.grocery.store.model.CartItem;
import com.grocery.store.model.Order;
import com.grocery.store.model.OrderItem;

@Repository
public class OrderDAO {

    @Autowired
    private DatabaseConfig databaseConfig;

    @Autowired
    private CartDAO cartDAO;

    @Autowired
    private BillDAO billDAO;


    // =========================================================
    // CREATE ORDER FROM CART
    // =========================================================

    public Order createOrderFromCart(int customerId, Cart cart) {

        String insertOrderSql =
                "INSERT INTO orders (customer_id, total_amount, status) " +
                "VALUES (?, ?, ?)";

        String insertOrderItemSql =
                "INSERT INTO order_item " +
                "(order_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

        // IMPORTANT:
        // Database column is "stock", NOT "stock_quantity"
        String updateStockSql =
                "UPDATE products " +
                "SET stock = stock - ? " +
                "WHERE product_id = ? AND stock >= ?";

        Connection conn = null;

        try {

            conn = databaseConfig.getConnection();

            // Start transaction
            conn.setAutoCommit(false);

            int orderId;

            // -------------------------------------------------
            // 1. CREATE ORDER
            // -------------------------------------------------

            try (PreparedStatement orderStmt =
                         conn.prepareStatement(
                                 insertOrderSql,
                                 Statement.RETURN_GENERATED_KEYS)) {

                orderStmt.setInt(1, customerId);
                orderStmt.setBigDecimal(2, cart.getTotalPrice());
                orderStmt.setString(3, "PLACED");

                orderStmt.executeUpdate();

                try (ResultSet rs = orderStmt.getGeneratedKeys()) {

                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException(
                                "Creating order failed, no order ID obtained.");
                    }
                }
            }


            // -------------------------------------------------
            // 2. INSERT ORDER ITEMS
            // 3. REDUCE PRODUCT STOCK
            // -------------------------------------------------

            try (PreparedStatement itemStmt =
                         conn.prepareStatement(insertOrderItemSql);

                 PreparedStatement stockStmt =
                         conn.prepareStatement(updateStockSql)) {

                for (CartItem cartItem : cart.getItems()) {

                    // Reduce stock
                    stockStmt.setInt(1, cartItem.getQuantity());
                    stockStmt.setInt(2, cartItem.getProductId());
                    stockStmt.setInt(3, cartItem.getQuantity());

                    int updatedRows = stockStmt.executeUpdate();

                    if (updatedRows == 0) {

                        throw new SQLException(
                                "Insufficient stock for product ID: "
                                        + cartItem.getProductId());
                    }


                    // Insert order item
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, cartItem.getProductId());
                    itemStmt.setInt(3, cartItem.getQuantity());
                    itemStmt.setBigDecimal(4, cartItem.getPrice());

                    itemStmt.executeUpdate();
                }
            }


            // -------------------------------------------------
            // 4. CREATE BILL
            // -------------------------------------------------

            billDAO.createBillInTransaction(
                    conn,
                    orderId,
                    cart.getTotalPrice()
            );


            // -------------------------------------------------
            // 5. CLEAR CART
            // -------------------------------------------------

            cartDAO.clearCart(
                    conn,
                    cart.getId()
            );


            // -------------------------------------------------
            // 6. COMMIT TRANSACTION
            // -------------------------------------------------

            conn.commit();


            // -------------------------------------------------
            // 7. RETURN CREATED ORDER
            // -------------------------------------------------

            return getOrderById(orderId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Failed to retrieve created order.")
                    );


        } catch (SQLException e) {

            // Rollback everything if any operation fails
            if (conn != null) {

                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Ignore rollback exception
                }
            }

            throw new RuntimeException(
                    "Order processing failed: " + e.getMessage(),
                    e
            );

        } finally {

            if (conn != null) {

                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    // Ignore close exception
                }
            }
        }
    }


    // =========================================================
    // GET ORDERS BY CUSTOMER
    // =========================================================

    public List<Order> getOrdersByCustomerId(int customerId) {

        List<Order> orders = new ArrayList<>();

        String sql =
                "SELECT order_id, customer_id, order_date, " +
                "total_amount, status " +
                "FROM orders " +
                "WHERE customer_id = ? " +
                "ORDER BY order_date DESC";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Order order = extractOrderFromResultSet(rs);

                    order.setItems(
                            getOrderItems(
                                    conn,
                                    order.getId()
                            )
                    );

                    orders.add(order);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error fetching orders for customer: "
                            + e.getMessage(),
                    e
            );
        }

        return orders;
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    public Optional<Order> getOrderById(int orderId) {

        String sql =
                "SELECT order_id, customer_id, order_date, " +
                "total_amount, status " +
                "FROM orders " +
                "WHERE order_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Order order = extractOrderFromResultSet(rs);

                    order.setItems(
                            getOrderItems(
                                    conn,
                                    orderId
                            )
                    );

                    return Optional.of(order);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error fetching order by id: "
                            + e.getMessage(),
                    e
            );
        }

        return Optional.empty();
    }


    // =========================================================
    // GET ORDER ITEMS
    // =========================================================

    private List<OrderItem> getOrderItems(
            Connection conn,
            int orderId) throws SQLException {

        List<OrderItem> items = new ArrayList<>();

        String sql =
                "SELECT oi.order_item_id, " +
                "oi.order_id, " +
                "oi.product_id, " +
                "oi.quantity, " +
                "oi.price, " +
                "p.name AS product_name " +
                "FROM order_item oi " +
                "JOIN products p " +
                "ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ?";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    OrderItem item = new OrderItem(

                            rs.getInt("order_item_id"),

                            rs.getInt("order_id"),

                            rs.getInt("product_id"),

                            rs.getString("product_name"),

                            rs.getInt("quantity"),

                            rs.getBigDecimal("price")
                    );

                    items.add(item);
                }
            }
        }

        return items;
    }


    // =========================================================
    // CONVERT RESULTSET TO ORDER
    // =========================================================

    private Order extractOrderFromResultSet(
            ResultSet rs) throws SQLException {

        Timestamp timestamp =
                rs.getTimestamp("order_date");

        LocalDateTime orderDate;

        if (timestamp != null) {
            orderDate = timestamp.toLocalDateTime();
        } else {
            orderDate = LocalDateTime.now();
        }

        return new Order(

                rs.getInt("order_id"),

                rs.getInt("customer_id"),

                orderDate,

                rs.getBigDecimal("total_amount"),

                rs.getString("status")
        );
    }
}