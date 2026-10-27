package com.grocery.store.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {

    private Integer billId;
    private Integer orderId;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal deliveryCharge;
    private BigDecimal totalAmount;
    private LocalDateTime billDate;

    public Bill() {
    }

    public Bill(
            Integer billId,
            Integer orderId,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal deliveryCharge,
            BigDecimal totalAmount,
            LocalDateTime billDate) {

        this.billId = billId;
        this.orderId = orderId;
        this.subtotal = subtotal;
        this.tax = tax;
        this.deliveryCharge = deliveryCharge;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(BigDecimal deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }
}