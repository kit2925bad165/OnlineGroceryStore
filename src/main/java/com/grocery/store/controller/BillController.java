package com.grocery.store.controller;

import com.grocery.store.model.Bill;
import com.grocery.store.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Bill> getBillByOrderId(@PathVariable int orderId) {
        return ResponseEntity.ok(billService.getBillByOrderId(orderId));
    }
}
