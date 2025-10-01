package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.model.Sales;
import com.noppo.agroflow_noppo.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {

    @Autowired
    private SalesService salesService;

    // Endpoint to approve a request and create a sale
    @PostMapping("/approve")
    public ResponseEntity<?> approveSale(
            @RequestBody Sales sale,
            @RequestParam Integer saleRequestId) {
        try {
            Sales newSale = salesService.approveSaleRequest(sale, saleRequestId);
            return new ResponseEntity<>(newSale, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}