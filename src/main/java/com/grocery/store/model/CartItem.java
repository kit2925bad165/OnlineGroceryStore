package com.grocery.store.model;

import java.math.BigDecimal;

public class CartItem {
    private Integer id;
    private Integer cartId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    public CartItem() {
    }

    public CartItem(Integer id, Integer cartId, Integer productId, String productName, Integer quantity, BigDecimal price) {
        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price != null && quantity != null ? price.multiply(new BigDecimal(quantity)) : BigDecimal.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.price != null && quantity != null) {
            this.subtotal = this.price.multiply(new BigDecimal(quantity));
        }
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        if (price != null && this.quantity != null) {
            this.subtotal = price.multiply(new BigDecimal(this.quantity));
        }
    }

    public BigDecimal getSubtotal() {
        if (price != null && quantity != null) {
            return price.multiply(new BigDecimal(quantity));
        }
        return subtotal != null ? subtotal : BigDecimal.ZERO;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
