package com.grocery.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grocery.store.dao.CartDAO;
import com.grocery.store.dao.CustomerDAO;
import com.grocery.store.dao.ProductDAO;
import com.grocery.store.exception.BadRequestException;
import com.grocery.store.exception.ResourceNotFoundException;
import com.grocery.store.model.Cart;
import com.grocery.store.model.CartItem;
import com.grocery.store.model.Product;

@Service
public class CartService {

    @Autowired
    private CartDAO cartDAO;

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private ProductDAO productDAO;

    public CartItem addItemToCart(int customerId, Integer productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new BadRequestException("Valid productId and positive quantity are required.");
        }

        customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId));

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        Cart cart = cartDAO.getOrCreateCartForCustomer(customerId);

        CartItem existingItem = null;

        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        int finalQuantity = quantity;

        if (existingItem != null) {
            finalQuantity = existingItem.getQuantity() + quantity;
        }

        if (product.getStockQuantity() < finalQuantity) {
            throw new BadRequestException(
                    "Insufficient stock available for product: " +
                    product.getName() +
                    ". Available: " +
                    product.getStockQuantity());
        }

        return cartDAO.addOrUpdateCartItem(
                cart.getId(),
                productId,
                quantity,
                product.getPrice()
        );
    }

    public Cart getCartByCustomerId(int customerId) {
        customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId));

        return cartDAO.getOrCreateCartForCustomer(customerId);
    }

    public CartItem updateCartItemQuantity(
            int customerId,
            int cartItemId,
            int newQuantity) {

        if (newQuantity <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero.");
        }

        Cart cart = getCartByCustomerId(customerId);

        CartItem cartItem = cartDAO.findCartItemById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItemId));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new ResourceNotFoundException(
                    "Cart item does not belong to this customer.");
        }

        Product product = productDAO.findById(cartItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product no longer exists."));

        if (product.getStockQuantity() < newQuantity) {
            throw new BadRequestException(
                    "Insufficient stock available. Available: " +
                    product.getStockQuantity());
        }

        cartDAO.updateCartItemQuantity(cartItemId, newQuantity);

        return cartDAO.findCartItemById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found after update."));
    }

    public void removeItemFromCart(int customerId, int cartItemId) {

        Cart cart = getCartByCustomerId(customerId);

        CartItem cartItem = cartDAO.findCartItemById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItemId));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new ResourceNotFoundException(
                    "Cart item does not belong to this customer.");
        }

        boolean removed = cartDAO.removeCartItem(cartItemId);

        if (!removed) {
            throw new RuntimeException(
                    "Failed to remove item from cart.");
        }
    }
}