package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.dto.*;
import com.noppo.agroflow_noppo.model.CropPlan;
import com.noppo.agroflow_noppo.model.Field;
import com.noppo.agroflow_noppo.model.Harvest;
import com.noppo.agroflow_noppo.repo.CropPlanRepo;
import com.noppo.agroflow_noppo.repo.FarmerRepo;
import com.noppo.agroflow_noppo.repo.FieldRepo;
import com.noppo.agroflow_noppo.repo.HarvestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; // <-- FIX 4a
import java.util.List;
import java.util.Map; // <-- FIX 1
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired private FarmerRepo farmerRepository;
    @Autowired private CropPlanRepo cropPlanRepository;
    @Autowired private FieldRepo fieldRepository;
    @Autowired private HarvestRepo harvestRepository;

    @Transactional(readOnly = true)
    public AnalyticsDTO getAnalyticsForOfficer(Integer officerId) {
        AnalyticsDTO dto = new AnalyticsDTO();
        int currentYear = LocalDate.now().getYear();
        // --- 1. Top-Level Stats ---
        dto.setTotalFarmers(farmerRepository.countByOfficerId(officerId));
        dto.setOngoingCropPlans(cropPlanRepository.countByOfficerIdAndStatusAndYear(officerId, "Ongoing", currentYear));
        dto.setHarvestedCropPlans(cropPlanRepository.countByOfficerIdAndStatusAndYear(officerId, "Harvested", currentYear));

        // --- 2. Ongoing Crop Plans List ---
        List<CropPlan> ongoingPlans = cropPlanRepository.findAllByFarmer_Officer_IdAndStatusOrderByExpectedHarvestDateAsc(officerId, "Ongoing");
        List<OngoingCropPlanDTO> ongoingPlanDTOs = ongoingPlans.stream().map(plan -> {
            OngoingCropPlanDTO planDto = new OngoingCropPlanDTO();
            planDto.setFarmerName(plan.getFarmer().getName());
            planDto.setCropName(plan.getCrop().getName());
            planDto.setFieldName(plan.getField().getName());
            planDto.setPlantingDate(plan.getPlantingDate());

            LocalDate harvestDate = plan.getExpectedHarvestDate();
            planDto.setExpectedHarvestDate(harvestDate);

            // --- THIS IS THE FIX ---
            // Only calculate 'daysLeft' if the harvest date is not null.
            if (harvestDate != null) {
                planDto.setDaysLeft(ChronoUnit.DAYS.between(LocalDate.now(), harvestDate));
            } else {
                // Provide a default or placeholder value if the date is null.
                planDto.setDaysLeft(-1); // Or Long.MAX_VALUE, or another indicator
            }

            return planDto;
        }).collect(Collectors.toList());
        dto.setOngoingCropPlansList(ongoingPlanDTOs);

        // --- 3. Unused Fields List ---
        List<Field> unusedFields = fieldRepository.findFieldsWithNoOngoingCropPlan(officerId);
        List<UnusedFieldDTO> unusedFieldDTOs = unusedFields.stream().map(field -> {
            UnusedFieldDTO fieldDto = new UnusedFieldDTO();
            fieldDto.setFarmerName(field.getFarmer().getName());
            fieldDto.setFieldName(field.getName());
            fieldDto.setSize(field.getSize());
            return fieldDto;
        }).collect(Collectors.toList());
        dto.setUnusedFieldsList(unusedFieldDTOs);

        // --- 4. REWRITTEN Overall Harvest Summary ---
        List<Harvest> allHarvests = harvestRepository.findAllHarvestsByOfficerIdAndYear(officerId, currentYear);

        Map<String, HarvestSummaryDTO> summaryMap = allHarvests.stream()
                .collect(Collectors.groupingBy(
                        harvest -> harvest.getCropPlan().getCrop().getName() + "-" + harvest.getCropPlan().getSeason(), // <-- FIX 2
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Harvest firstHarvestInGroup = list.get(0); // <-- FIX 2
                                    String cropName = firstHarvestInGroup.getCropPlan().getCrop().getName();
                                    String season = firstHarvestInGroup.getCropPlan().getSeason();

                                    double totalArea = list.stream()
                                            .map(h -> h.getCropPlan().getField().getSize())
                                            .mapToDouble(BigDecimal::doubleValue) // <-- FIX 3
                                            .sum();

                                    BigDecimal totalYield = list.stream()
                                            .map(Harvest::getQuantity) // <-- FIX 3
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                    return new HarvestSummaryDTO(cropName, totalArea, totalYield, season);
                                }
                        )
                ));

        List<HarvestSummaryDTO> summaryList = new ArrayList<>(summaryMap.values()); // <-- FIX 4b

        dto.setHarvestSummaryList(summaryList);

        return dto;
    }
    // --- ADD THIS NEW METHOD FOR THE SEARCH LOGIC ---
    @Transactional(readOnly = true)
    public List<CropSearchResultDTO> searchOngoingPlansByCrop(Integer officerId, String cropName) {
        // 1. Fetch the filtered list of CropPlan entities from the repository.
        List<CropPlan> searchResults = cropPlanRepository
                .findAllByFarmer_Officer_IdAndStatusAndCrop_NameContainingIgnoreCase(officerId, "Ongoing", cropName);

        // 2. Map the results to the DTO.
        return searchResults.stream().map(plan -> {
            CropSearchResultDTO dto = new CropSearchResultDTO();
            dto.setCropName(plan.getCrop().getName());
            dto.setFarmerName(plan.getFarmer().getName());
            dto.setFieldName(plan.getField().getName());
            dto.setPlantingDate(plan.getPlantingDate());

            // --- APPLY THE SAME FIX HERE ---
            LocalDate harvestDate = plan.getExpectedHarvestDate();
            if (harvestDate != null) {
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), harvestDate);
                dto.setDaysLeft(daysLeft);
            } else {
                dto.setDaysLeft(-1); // Or another indicator of missing data
            }

            return dto;
        }).collect(Collectors.toList());
    }
}