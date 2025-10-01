package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Keep this
import java.math.BigDecimal;

@Data
@NoArgsConstructor // Keep the no-args constructor for flexibility
public class MainReportRowDTO {
    private String cropName;
    private BigDecimal totalHarvestKg;
    private BigDecimal totalAreaAcres; // Changed to BigDecimal to match the SUM() result
    private Long numberOfFarmers;

    // --- ADD THIS EXACT CONSTRUCTOR ---
    // The parameter types and their order MUST match the JPQL query's SELECT clause.
    public MainReportRowDTO(String cropName, BigDecimal totalHarvestKg, BigDecimal totalAreaAcres, Long numberOfFarmers) {
        this.cropName = cropName;
        this.totalHarvestKg = totalHarvestKg;
        this.totalAreaAcres = totalAreaAcres;
        this.numberOfFarmers = numberOfFarmers;
    }
}