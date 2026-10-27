package com.grocery.store.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.grocery.store.config.DatabaseConfig;
import com.grocery.store.model.Cart;
import com.grocery.store.model.CartItem;

@Repository
public class CartDAO {

    @Autowired
    private DatabaseConfig databaseConfig;

    // Get existing cart or create a new cart
    public Cart getOrCreateCartForCustomer(int customerId) {

        Optional<Cart> existingCart = getCartByCustomerId(customerId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        String sql = "INSERT INTO cart (customer_id) VALUES (?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.executeUpdate();

            Cart cart = new Cart();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cart.setId(rs.getInt(1));
                }
            }

            cart.setCustomerId(customerId);

            return cart;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error creating cart for customer: " + e.getMessage(), e);
        }
    }

    // Get cart by customer ID
    public Optional<Cart> getCartByCustomerId(int customerId) {

        String cartSql =
                "SELECT cart_id, customer_id " +
                "FROM cart " +
                "WHERE customer_id = ?";

        String itemsSql =
                "SELECT ci.cart_item_id, " +
                "ci.cart_id, " +
                "ci.product_id, " +
                "ci.quantity, " +
                "p.name AS product_name, " +
                "p.price " +
                "FROM cart_item ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement cartStmt =
                     conn.prepareStatement(cartSql)) {

            cartStmt.setInt(1, customerId);

            try (ResultSet cartRs = cartStmt.executeQuery()) {

                if (cartRs.next()) {

                    Cart cart = new Cart(
                            cartRs.getInt("cart_id"),
                            cartRs.getInt("customer_id")
                    );

                    List<CartItem> items = new ArrayList<>();

                    try (PreparedStatement itemStmt =
                                 conn.prepareStatement(itemsSql)) {

                        itemStmt.setInt(1, cart.getId());

                        try (ResultSet itemRs =
                                     itemStmt.executeQuery()) {

                            while (itemRs.next()) {

                                CartItem item = new CartItem(
                                        itemRs.getInt("cart_item_id"),
                                        itemRs.getInt("cart_id"),
                                        itemRs.getInt("product_id"),
                                        itemRs.getString("product_name"),
                                        itemRs.getInt("quantity"),
                                        itemRs.getBigDecimal("price")
                                );

                                items.add(item);
                            }
                        }
                    }

                    cart.setItems(items);

                    return Optional.of(cart);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error fetching cart for customer: "
                            + e.getMessage(), e);
        }

        return Optional.empty();
    }

    // Add a new item or increase quantity of existing item
    public CartItem addOrUpdateCartItem(
            int cartId,
            int productId,
            int quantity,
            BigDecimal price) {

        String checkSql =
                "SELECT cart_item_id, quantity " +
                "FROM cart_item " +
                "WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement checkStmt =
                     conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, cartId);
            checkStmt.setInt(2, productId);

            try (ResultSet rs = checkStmt.executeQuery()) {

                if (rs.next()) {

                    int existingId =
                            rs.getInt("cart_item_id");

                    int newQuantity =
                            rs.getInt("quantity") + quantity;

                    String updateSql =
                            "UPDATE cart_item " +
                            "SET quantity = ? " +
                            "WHERE cart_item_id = ?";

                    try (PreparedStatement updateStmt =
                                 conn.prepareStatement(updateSql)) {

                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setInt(2, existingId);

                        updateStmt.executeUpdate();
                    }

                    return findCartItemById(existingId)
                            .orElse(null);
                }
            }

            String insertSql =
                    "INSERT INTO cart_item " +
                    "(cart_id, product_id, quantity) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement insertStmt =
                         conn.prepareStatement(
                                 insertSql,
                                 Statement.RETURN_GENERATED_KEYS)) {

                insertStmt.setInt(1, cartId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);

                insertStmt.executeUpdate();

                int generatedId = 0;

                try (ResultSet rs =
                             insertStmt.getGeneratedKeys()) {

                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }

                return findCartItemById(generatedId)
                        .orElse(null);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error adding item to cart: "
                            + e.getMessage(), e);
        }
    }

    // Find cart item by ID
    public Optional<CartItem> findCartItemById(
            int cartItemId) {

        String sql =
                "SELECT ci.cart_item_id, " +
                "ci.cart_id, " +
                "ci.product_id, " +
                "ci.quantity, " +
                "p.name AS product_name, " +
                "p.price " +
                "FROM cart_item ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_item_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, cartItemId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    CartItem item = new CartItem(
                            rs.getInt("cart_item_id"),
                            rs.getInt("cart_id"),
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price")
                    );

                    return Optional.of(item);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding cart item: "
                            + e.getMessage(), e);
        }

        return Optional.empty();
    }

    // Update cart item quantity
    public boolean updateCartItemQuantity(
            int cartItemId,
            int newQuantity) {

        String sql =
                "UPDATE cart_item " +
                "SET quantity = ? " +
                "WHERE cart_item_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, cartItemId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error updating cart item quantity: "
                            + e.getMessage(), e);
        }
    }

    // Remove cart item
    public boolean removeCartItem(int cartItemId) {

        String sql =
                "DELETE FROM cart_item " +
                "WHERE cart_item_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, cartItemId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error removing cart item: "
                            + e.getMessage(), e);
        }
    }

    // Clear all items from a cart
    public void clearCart(
            Connection conn,
            int cartId) throws SQLException {

        String sql =
                "DELETE FROM cart_item " +
                "WHERE cart_id = ?";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }
}