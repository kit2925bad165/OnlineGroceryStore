package com.grocery.store.controller;

import com.grocery.store.model.Cart;
import com.grocery.store.model.CartItem;
import com.grocery.store.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartItem> addItemToCart(
            @PathVariable int customerId,
            @RequestBody CartItem request) {
        CartItem addedItem = cartService.addItemToCart(customerId, request.getProductId(), request.getQuantity());
        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCartByCustomerId(@PathVariable int customerId) {
        return ResponseEntity.ok(cartService.getCartByCustomerId(customerId));
    }

    @PutMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(
            @PathVariable int customerId,
            @PathVariable int cartItemId,
            @RequestBody Map<String, Integer> request) {
        Integer newQuantity = request.get("quantity");
        CartItem updated = cartService.updateCartItemQuantity(customerId, cartItemId, newQuantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<Map<String, String>> removeItemFromCart(
            @PathVariable int customerId,
            @PathVariable int cartItemId) {
        cartService.removeItemFromCart(customerId, cartItemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart item removed successfully");
        return ResponseEntity.ok(response);
    }
}
