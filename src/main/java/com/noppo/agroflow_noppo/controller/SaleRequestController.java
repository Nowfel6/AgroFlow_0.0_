package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.dto.SaleRequestDetailsDTO;
import com.noppo.agroflow_noppo.dto.SaleRequestSummaryDTO;
import com.noppo.agroflow_noppo.model.SaleRequest;
import com.noppo.agroflow_noppo.service.SaleRequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salerequests")
@CrossOrigin(origins = "*")
public class SaleRequestController {

    @Autowired
    private SaleRequestService saleRequestService;

    // Endpoint will be POST /api/salerequests/create?farmerId=1&officerId=1&harvestId=1
    @PostMapping("/create")
    public ResponseEntity<?> createSaleRequest(
            @RequestBody SaleRequest saleRequest,
            @RequestParam Integer farmerId,
            @RequestParam Integer officerId,
            @RequestParam Integer harvestId) {

        try {
            SaleRequest newRequest = saleRequestService.createSaleRequest(saleRequest, farmerId, officerId, harvestId);
            return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<SaleRequestSummaryDTO>> getRequestsForOfficer(@PathVariable Integer officerId) {
        List<SaleRequestSummaryDTO> requests = saleRequestService.getSaleRequestsForOfficer(officerId);
        return ResponseEntity.ok(requests);
    }
    // In SaleRequestController.java

    @GetMapping("/{requestId}/details")
    public ResponseEntity<SaleRequestDetailsDTO> getRequestDetails(@PathVariable Integer requestId) {
        try {
            SaleRequestDetailsDTO details = saleRequestService.getSaleRequestDetails(requestId);
            return ResponseEntity.ok(details);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}