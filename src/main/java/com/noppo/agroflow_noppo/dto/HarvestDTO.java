package com.noppo.agroflow_noppo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class HarvestDTO {
    private int id;
    private LocalDate harvest_date;
    private BigDecimal quantity;
    private String quality_grade;
    private String storage_status;
    private String storage_location;
    private String cropName;
    private boolean saleRequested;
}
