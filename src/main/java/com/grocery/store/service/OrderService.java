package com.grocery.store.service;

import com.grocery.store.dao.CartDAO;
import com.grocery.store.dao.CustomerDAO;
import com.grocery.store.dao.OrderDAO;
import com.grocery.store.exception.BadRequestException;
import com.grocery.store.exception.ResourceNotFoundException;
import com.grocery.store.model.Cart;
import com.grocery.store.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private CartDAO cartDAO;

    @Autowired
    private CustomerDAO customerDAO;

    public Order placeOrder(int customerId) {
        customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Cart cart = cartDAO.getCartByCustomerId(customerId)
                .orElseThrow(() -> new BadRequestException("Shopping cart is empty. Add items before placing an order."));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Shopping cart is empty. Add items before placing an order.");
        }

        return orderDAO.createOrderFromCart(customerId, cart);
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        return orderDAO.getOrdersByCustomerId(customerId);
    }

    public Order getOrderById(int orderId) {
        return orderDAO.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
}
