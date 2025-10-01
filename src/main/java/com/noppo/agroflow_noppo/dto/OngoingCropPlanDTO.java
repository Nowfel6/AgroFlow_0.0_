package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OngoingCropPlanDTO {
    private String farmerName;
    private String cropName;
    private String fieldName;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private long daysLeft; // The calculated days remaining
}