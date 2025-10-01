package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.dto.*;
import com.noppo.agroflow_noppo.model.Crop;
import com.noppo.agroflow_noppo.model.KrishiOfficer;
import com.noppo.agroflow_noppo.model.Reports;
import com.noppo.agroflow_noppo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    @Autowired private ReportsRepo reportsRepository;
    @Autowired private SalesRepo salesRepository;
    @Autowired private OfficerRepo officerRepository;
    @Autowired private CropRepo cropRepository;
    @Autowired private CropPlanRepo cropPlanRepo;

    // Method to get an existing report or generate a new one with calculated data
    @Transactional(readOnly = true)
    public ReportsDTO getOrCreateReport(Integer officerId, Integer cropId, String sessionYear) {

        // --- STEP 1: ALWAYS CALCULATE THE LATEST STATS FROM SALES DATA ---
        int year = Integer.parseInt(sessionYear);

        BigDecimal totalYield = salesRepository.sumTotalYieldByOfficerAndCropAndYear(officerId, cropId, year);
        Double totalArea = salesRepository.sumTotalAreaByOfficerAndCropAndYear(officerId, cropId, year);
        Long farmerCount = salesRepository.countDistinctFarmersByOfficerAndCropAndYear(officerId, cropId, year);

        // Create a new DTO to hold these fresh, calculated values.
        ReportsDTO reportData = new ReportsDTO();
        reportData.setSession_year(sessionYear);
        reportData.setOfficerId(officerId);
        reportData.setCropId(cropId);
        reportData.setTotal_yield(totalYield == null ? BigDecimal.ZERO : totalYield);
        reportData.setTotal_area_cultivated(totalArea == null ? 0.0 : totalArea);
        reportData.setNo_of_farmer(farmerCount == null ? 0L : farmerCount);

        KrishiOfficer officer = officerRepository.findById(officerId).orElse(null);
        if (officer != null) {
            reportData.setRegion(officer.getRegion_assigned());
        }

        // --- STEP 2: CHECK IF A REPORT WAS PREVIOUSLY SAVED ---
        // We do this to get the ID for a potential UPDATE operation later.
        Optional<Reports> existingReport = reportsRepository
                .findByOfficer_IdAndCrop_IdAndSessionYear(officerId, cropId, sessionYear);

        // If an old report exists, set its ID on our DTO.
        // The DTO will now contain the LATEST calculated data, plus the ID of the OLD record.
        if (existingReport.isPresent()) {
            reportData.setId(existingReport.get().getId());
        }
        // If no report exists, the ID will remain null, signaling a CREATE operation.

        // --- STEP 3: RETURN THE DTO ---
        // The frontend will receive a DTO with the most up-to-date calculated numbers.
        return reportData;
    }

    // Method to save or update a report
    @Transactional
    public ReportsDTO saveOrUpdateReport(ReportsDTO dto) {
        KrishiOfficer officer = officerRepository.findById(dto.getOfficerId()).orElseThrow(() -> new RuntimeException("Officer not found"));
        Crop crop = cropRepository.findById(dto.getCropId()).orElseThrow(() -> new RuntimeException("Crop not found"));

        // Find existing report or create a new one
        Reports report = reportsRepository
                .findByOfficer_IdAndCrop_IdAndSessionYear(dto.getOfficerId(), dto.getCropId(), dto.getSession_year())
                .orElse(new Reports());

        // Populate/update the entity from the DTO
        report.setOfficer(officer);
        report.setCrop(crop);
        report.setSessionYear(dto.getSession_year());
        report.setRegion(dto.getRegion());
        report.setTotal_yield(dto.getTotal_yield());
        report.setTotal_area_cultivated(BigDecimal.valueOf(dto.getTotal_area_cultivated()));
        report.setNo_of_farmer(dto.getNo_of_farmer());

        Reports savedReport = reportsRepository.save(report);
        return mapEntityToDto(savedReport);
    }

    // Helper to map Entity to DTO
    private ReportsDTO mapEntityToDto(Reports report) {
        ReportsDTO dto = new ReportsDTO();
        dto.setId(report.getId());
        dto.setOfficerId(report.getOfficer().getId());
        dto.setCropId(report.getCrop().getId());
        dto.setSession_year(report.getSessionYear());
        dto.setRegion(report.getRegion());
        dto.setTotal_yield(report.getTotal_yield());
        dto.setTotal_area_cultivated(report.getTotal_area_cultivated().doubleValue());
        dto.setNo_of_farmer(report.getNo_of_farmer());
        return dto;
    }
    // --- Service for Section 1: Summary Cards ---
    @Transactional(readOnly = true)
    public AdvancedReportDTO getSummaryCardData(String sessionYear) {
        AdvancedReportDTO dto = new AdvancedReportDTO();

        // --- THIS IS THE NEW LOGIC FOR HANDLING THE YEAR ---
        // We assume sessionYear is like "2024" or "2024-25". We extract the starting year.
        int year = Integer.parseInt(sessionYear.substring(0, 4));
        LocalDate startDate = LocalDate.of(year, 1, 1); // January 1st of that year
        LocalDate endDate = LocalDate.of(year, 12, 31); // December 31st of that year

        // 1. Total Farmers Engaged (This logic is correct and uses the 'year' int)
        Long totalFarmers = salesRepository.countDistinctFarmersByYear(year);
        dto.setTotalFarmersEngaged(totalFarmers != null ? totalFarmers : 0L);

        // 2. Total Cultivated Area (CORRECTED)
        // Now we call the new method with the start and end dates.
        Double totalArea = cropPlanRepo.sumAreaOfDistinctFieldsByDateRange(startDate, endDate);
        dto.setTotalCultivatedArea(totalArea != null ? totalArea : 0.0);

        // 3. Total Harvest (This uses the sessionYear String, which is correct for the Reports table)
        BigDecimal totalHarvest = reportsRepository.sumTotalHarvestByYear(sessionYear);
        dto.setTotalHarvestKg(totalHarvest != null ? totalHarvest : BigDecimal.ZERO);

        // 4. Top Crop (This also uses the sessionYear String, which is correct)
        String topCrop = reportsRepository.findTopCropByYear(sessionYear);
        dto.setTopCropByHarvest(topCrop != null ? topCrop : "N/A");

        return dto;
    }

    // --- Service for Section 2: Main Table ---
    public List<MainReportRowDTO> getMainReport(String year, String region) {
        if ("All Regions".equalsIgnoreCase(region)) {
            return reportsRepository.getNationalReportByYear(year);
        } else {
            return reportsRepository.getRegionalReportByYear(year, region);
        }
    }

    // --- Service for Section 3: Top Regions Table ---
    public List<TopRegionDTO> getTopRegionsReport(String year, Integer cropId) {
        return reportsRepository.getTopRegionsForCropByYear(year, cropId);
    }

    // --- Service for Section 4: Chart Data ---
    public List<ChartDataDTO> getChartData(Integer cropId) {
        List<String> lastThreeYears = reportsRepository.findDistinctSessionYears().stream().limit(3).collect(Collectors.toList());
        return lastThreeYears.stream().map(year -> {
            BigDecimal total = reportsRepository.getNationalHarvestForCropByYear(cropId, year);
            return new ChartDataDTO(year, total != null ? total : BigDecimal.ZERO);
        }).collect(Collectors.toList());
    }

    // --- Service for Populating Filters ---
    public FilterDataDTO getFilterData() {
        FilterDataDTO dto = new FilterDataDTO();
        dto.setSessionYears(reportsRepository.findDistinctSessionYears());
        dto.setRegions(reportsRepository.findDistinctRegions());
        List<Crop> crops = cropRepository.findAll();
        // You can map crops to a simpler CropDTO if you prefer
        dto.setCrops(crops);
        return dto;
    }

}