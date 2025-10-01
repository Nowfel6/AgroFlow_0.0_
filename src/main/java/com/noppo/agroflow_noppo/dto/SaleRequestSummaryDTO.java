package com.noppo.agroflow_noppo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleRequestSummaryDTO {
    private Integer id; // The ID of the request itself
    private String cropName;
    private String farmerName;
    private BigDecimal harvestAmount; // The total amount harvested
}