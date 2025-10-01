package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.model.SaleRequest;
import com.noppo.agroflow_noppo.model.Sales;
import com.noppo.agroflow_noppo.repo.SaleRequestRepo;
import com.noppo.agroflow_noppo.repo.SalesRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesService {

    @Autowired
    private SalesRepo salesRepository;

    @Autowired
    private SaleRequestRepo saleRequestRepository;

    @Transactional
    public Sales approveSaleRequest(Sales sale, Integer saleRequestId) {
        // 1. Find the original SaleRequest to link it to.
        SaleRequest request = saleRequestRepository.findById(saleRequestId)
                .orElseThrow(() -> new EntityNotFoundException("SaleRequest not found with ID: " + saleRequestId));

        // 2. Business Rule: Check if this request has already been fulfilled.
        if (request.getSale() != null) {
            throw new IllegalStateException("This sale request has already been fulfilled.");
        }

        // 3. Set the relationship: The new Sale fulfills the request.
        sale.setSaleRequest(request);

        // 4. Save the new Sales record.
        return salesRepository.save(sale);
    }
}