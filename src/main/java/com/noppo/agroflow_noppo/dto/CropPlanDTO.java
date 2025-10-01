package com.noppo.agroflow_noppo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CropPlanDTO {
    private int id;
    private LocalDate planting_date;
    private LocalDate expected_harvest_date;
    private BigDecimal expected_yield;
    private String season;
    private String status;
    private String cropName;  // To hold the name of the associated Crop
    private String fieldName; // To hold the name of the associated Field
}
