package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AnalyticsDTO {
    // Top-level stats
    private long totalFarmers;
    private long ongoingCropPlans;
    private long harvestedCropPlans;

    // Table data
    private List<OngoingCropPlanDTO> ongoingCropPlansList;
    private List<UnusedFieldDTO> unusedFieldsList;
    private List<HarvestSummaryDTO> harvestSummaryList;
}