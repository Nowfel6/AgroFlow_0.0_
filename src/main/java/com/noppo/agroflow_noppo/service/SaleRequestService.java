package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.dto.MarketPriceDTO;
import com.noppo.agroflow_noppo.dto.SaleRequestDetailsDTO;
import com.noppo.agroflow_noppo.dto.SaleRequestSummaryDTO;
import com.noppo.agroflow_noppo.model.*;
import com.noppo.agroflow_noppo.repo.FarmerRepo;
import com.noppo.agroflow_noppo.repo.HarvestRepo;
import com.noppo.agroflow_noppo.repo.OfficerRepo;
import com.noppo.agroflow_noppo.repo.SaleRequestRepo;
import com.noppo.agroflow_noppo.repo.MarketPriceRepo;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleRequestService {

    @Autowired private SaleRequestRepo saleRequestRepository;
    @Autowired private FarmerRepo farmerRepository;
    @Autowired private OfficerRepo officerRepository;
    @Autowired private HarvestRepo harvestRepository;
    @Autowired private MarketPriceRepo marketPriceRepository;
    public SaleRequest createSaleRequest(SaleRequest request, Integer farmerId, Integer officerId, Integer harvestId) {
        // 1. Fetch the full, managed entities for all relationships.
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found with ID: " + farmerId));

        KrishiOfficer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new EntityNotFoundException("Officer not found with ID: " + officerId));

        Harvest harvest = harvestRepository.findById(harvestId)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + harvestId));

        // Security check: You might want to verify that the harvest actually belongs to the farmer.
        if (harvest.getCropPlan().getFarmer().getId() != farmerId) {
            throw new IllegalStateException("Harvest does not belong to the specified farmer.");
        }

        // 2. Set the full, managed objects on the new SaleRequest.
        request.setFarmer(farmer);
        request.setOfficer(officer);
        request.setHarvest(harvest);

        // 3. Save the fully constructed SaleRequest entity.
        return saleRequestRepository.save(request);
    }
    // --- NEW METHOD to get the summary list ---
    @Transactional(readOnly = true)
    public List<SaleRequestSummaryDTO> getSaleRequestsForOfficer(Integer officerId) {
        // You'll need to create this custom repository method
        List<SaleRequest> requests = saleRequestRepository.findAllByOfficerIdAndSaleIsNull(officerId);

        return requests.stream().map(req -> {
            SaleRequestSummaryDTO dto = new SaleRequestSummaryDTO();
            dto.setId(req.getId());
            dto.setFarmerName(req.getFarmer().getName());
            dto.setCropName(req.getHarvest().getCropPlan().getCrop().getName());
            dto.setHarvestAmount(req.getHarvest().getQuantity());
            return dto;
        }).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public SaleRequestDetailsDTO getSaleRequestDetails(Integer requestId) {
        // 1. Fetch the full SaleRequest entity. This is your single source of truth.
        SaleRequest request = saleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("SaleRequest not found with ID: " + requestId));

        // 2. Create the DTO to be returned.
        SaleRequestDetailsDTO detailsDTO = new SaleRequestDetailsDTO();

        // 3. Populate ALL fields of the DTO by traversing the entity relationships.
        // This must be done inside the @Transactional method to avoid lazy loading issues.

        // Basic Request Info
        detailsDTO.setId(request.getId());
        detailsDTO.setFarmerName(request.getFarmer().getName());
        detailsDTO.setCropName(request.getHarvest().getCropPlan().getCrop().getName()); // <-- THE MISSING LINE
        detailsDTO.setQuantity_requested(request.getQuantity_requested());
        detailsDTO.setPrice_per_kg_requested(request.getPrice_per_kg_requested());
        detailsDTO.setDelivery_status(request.getDelivery_status());

        // Payment Info
        detailsDTO.setPayment_mode_requested(request.getPayment_mode_requested());
        detailsDTO.setBank_name(request.getBank_name());
        detailsDTO.setAccount_holder_name(request.getAccount_holder_name());
        detailsDTO.setAccount_number(request.getAccount_number());
        detailsDTO.setIfsc_code(request.getIfsc_code());
        detailsDTO.setOnline_banking_no(request.getOnline_banking_no());

        // 4. Fetch and set the market price info.
        Integer cropId = request.getHarvest().getCropPlan().getCrop().getId();
        marketPriceRepository.findAllByCropId(cropId)
                .stream()
                .findFirst() // Safely get the first element from the list
                .ifPresent(marketPrice -> {
                    detailsDTO.setCurrentMarketPrice(marketPrice.getMarket_price_per_kg());
                });

        return detailsDTO;
    }
}