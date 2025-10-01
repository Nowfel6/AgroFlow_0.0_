package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class AdvancedReportDTO {
    // For Section 1: Summary Cards
    private long totalFarmersEngaged;
    private double totalCultivatedArea;
    private BigDecimal totalHarvestKg;
    private String topCropByHarvest;

    // For Section 2: Main Report Table
    private List<MainReportRowDTO> mainReport;

    // For Section 3: Top Regions Table
    private List<TopRegionDTO> topRegions;
}