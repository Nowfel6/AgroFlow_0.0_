package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.dto.*;
import com.noppo.agroflow_noppo.model.*;
import com.noppo.agroflow_noppo.repo.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FarmerProfileService {

    // Inject all the necessary repositories
    @Autowired private FarmerRepo farmerRepository;
    @Autowired private FieldRepo fieldRepository;
    @Autowired private CropPlanRepo cropPlanRepository;
    @Autowired private HarvestRepo harvestRepository;
    @Autowired private SalesRepo salesRepository;
    @Autowired private SaleRequestRepo saleRequestRepo;
    // ... your other autowired fields and existing methods ...

    // --- NEW METHOD TO GET THE FULL FARMER PROFILE ---
    @Transactional(readOnly = true)
    public FarmerProfileDTO getFarmerProfileById(Integer farmerId) {
        // 1. Fetch the core farmer object
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found with ID: " + farmerId));

        // 2. Fetch all related lists using the new repository methods
        List<Field> fields = fieldRepository.findAllByFarmerId(farmerId);
        List<CropPlan> cropPlans = cropPlanRepository.findAllByFarmerId(farmerId);
        List<Harvest> harvests = harvestRepository.findAllByFarmerId(farmerId);
        List<SaleRequest> saleRequests = saleRequestRepo.findAllByFarmerId(farmerId);
        List<Sales> sales = salesRepository.findAllByFarmerId(farmerId);
        // Use map() to get a Stream<BigDecimal> and then reduce() to sum them.
        BigDecimal totalAcres = fields.stream()
                .map(Field::getSize) // This creates a Stream<BigDecimal>
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Start with 0 and add each value


        // Count how many crop plans have the status "Ongoing"
        long activeCropPlans = cropPlans.stream()
                .filter(plan -> "Ongoing".equalsIgnoreCase(plan.getStatus()))
                .count();

        // Calculate total sales revenue by summing the 'final_quantity' * 'final_price_per_kg'
        BigDecimal totalSalesValue = sales.stream()
                .map(sale -> sale.getFinal_quantity().multiply(sale.getFinal_price_per_kg()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<FieldDTO> fieldDTOs = fields.stream().map(field -> {
            FieldDTO dto = new FieldDTO();
            dto.setId(field.getId());
            dto.setName(field.getName());
            dto.setLandmark(field.getLandmark());
            dto.setSize(field.getSize());
            dto.setIrrigation_type(field.getIrrigation_type());
            dto.setOwnership_type(field.getOwnership_type());
            dto.setSoil_type(field.getSoil_type());
            return dto;
        }).collect(Collectors.toList());
        List<CropPlanDTO> cropPlanDTOs = cropPlans.stream().map(plan -> {
            CropPlanDTO dto = new CropPlanDTO();
            dto.setId(plan.getId());
            dto.setCropName(plan.getCrop().getName()); // Safely access lazy-loaded data here
            dto.setFieldName(plan.getField().getName()); // And here
            dto.setPlanting_date(plan.getPlantingDate());
            dto.setExpected_harvest_date(plan.getExpectedHarvestDate());
            dto.setStatus(plan.getStatus());
            return dto;
        }).collect(Collectors.toList());
        // --- NEW LOGIC: Identify which harvests have been requested ---
        // 1. Create a Set of all Harvest IDs that are already in a SalesRequest.
        //    A Set is used for very fast lookups.
        Set<Integer> requestedHarvestIds = saleRequests.stream()
                .map(request -> request.getHarvest().getId())
                .collect(Collectors.toSet());
        List<HarvestDTO> harvestDTOS = harvests.stream().map(plan -> {
            HarvestDTO dto = new HarvestDTO();
            dto.setId(plan.getId());
            dto.setCropName(plan.getCropPlan().getCrop().getName()); // Safely access lazy-loaded data here
            dto.setQuantity(plan.getQuantity());
            dto.setQuality_grade(plan.getQuality_grade());
            dto.setStorage_location(plan.getStorage_location());
            dto.setStorage_status(plan.getStorage_status());
            dto.setHarvest_date(plan.getHarvestDate());
            dto.setSaleRequested(requestedHarvestIds.contains(plan.getId()));
            return dto;
        }).collect(Collectors.toList());
        List<SalesDTO> salesDTOS = sales.stream().map(plan -> {
            SalesDTO dto = new SalesDTO();
            dto.setId(plan.getId());
            dto.setCropName(plan.getSaleRequest().getHarvest().getCropPlan().getCrop().getName()); // Safely access lazy-loaded data here
            dto.setSale_date(plan.getSale_date());
            dto.setPayment_mode(plan.getPayment_mode());
            dto.setFinal_quantity(plan.getFinal_quantity());
            dto.setFinal_price_per_kg(plan.getFinal_price_per_kg());
            dto.setPayment_status(plan.getPayment_status());
            if (plan.getSaleRequest() != null) {
                dto.setDelivery_status(plan.getSaleRequest().getDelivery_status());
            }
            return dto;
        }).collect(Collectors.toList());
        // 3. Create and populate the DTO
        FarmerProfileDTO profileDTO = new FarmerProfileDTO();
        profileDTO.setId(farmer.getId());
        profileDTO.setName(farmer.getName());
        profileDTO.setContact(farmer.getContact());
        profileDTO.setNid(farmer.getNid());
        profileDTO.setVillage(farmer.getVillage());
        profileDTO.setUnion_name(farmer.getUnion_name());
        profileDTO.setDistrict(farmer.getDistrict());
        profileDTO.setTotalAcres(totalAcres.doubleValue());
        profileDTO.setActiveCropPlans(activeCropPlans);
        profileDTO.setTotalSalesYTD(totalSalesValue);
        profileDTO.setFields(fieldDTOs);
        profileDTO.setCropPlans(cropPlanDTOs);
        profileDTO.setHarvests(harvestDTOS);
        profileDTO.setSales(salesDTOS);

        return profileDTO;
    }
}