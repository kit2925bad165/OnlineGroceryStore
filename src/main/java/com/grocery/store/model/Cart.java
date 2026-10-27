package com.grocery.store.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Integer id;
    private Integer customerId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public Cart() {
    }

    public Cart(Integer id, Integer customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotalPrice();
    }

    public BigDecimal getTotalPrice() {
        calculateTotalPrice();
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    private void calculateTotalPrice() {
        if (items == null || items.isEmpty()) {
            this.totalPrice = BigDecimal.ZERO;
            return;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getSubtotal() != null) {
                sum = sum.add(item.getSubtotal());
            }
        }
        this.totalPrice = sum;
    }
}
