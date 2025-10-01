package com.noppo.agroflow_noppo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor // Important for the JPQL constructor expression
public class TopRegionDTO {
    private String regionName;
    private BigDecimal totalHarvestKg;
}