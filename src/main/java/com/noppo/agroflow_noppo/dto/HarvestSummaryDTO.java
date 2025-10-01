package com.noppo.agroflow_noppo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class HarvestSummaryDTO {
    private String cropName;
    private Double totalArea; // Sum of field sizes
    private BigDecimal totalYield; // Sum of harvest quantities
    private String season;
    public HarvestSummaryDTO(String cropName, Double totalArea, BigDecimal totalYield, String season) {
        this.cropName = cropName;
        this.totalArea = totalArea;
        this.totalYield = totalYield;
        this.season = season;
    }
}