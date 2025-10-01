package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CropSearchResultDTO {
    private String cropName;
    private String farmerName;
    private String fieldName;
    private LocalDate plantingDate;
    private long daysLeft; // Days left until expected harvest
}