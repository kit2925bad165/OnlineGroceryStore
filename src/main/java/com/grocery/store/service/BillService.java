package com.grocery.store.service;

import com.grocery.store.dao.BillDAO;
import com.grocery.store.dao.OrderDAO;
import com.grocery.store.exception.ResourceNotFoundException;
import com.grocery.store.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillService {

    @Autowired
    private BillDAO billDAO;

    @Autowired
    private OrderDAO orderDAO;

    public Bill getBillByOrderId(int orderId) {
        orderDAO.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return billDAO.getBillByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found for order id: " + orderId));
    }
}
