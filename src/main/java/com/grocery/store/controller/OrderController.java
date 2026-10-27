package com.grocery.store.controller;

import com.grocery.store.model.Order;
import com.grocery.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{customerId}")
    public ResponseEntity<Order> placeOrder(@PathVariable int customerId) {
        Order order = orderService.placeOrder(customerId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable int customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }
}
