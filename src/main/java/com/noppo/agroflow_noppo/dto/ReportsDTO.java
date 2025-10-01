package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReportsDTO {
    private Integer id; // Can be null if it's a new report
    private BigDecimal total_yield;
    private Double total_area_cultivated; // Sum of field sizes
    private String session_year;
    private String region;
    private Long no_of_farmer;
    private Integer officerId;
    private Integer cropId;
}