package com.noppo.agroflow_noppo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor // A constructor is useful for creating these in the service
public class ChartDataDTO {

    private String year;
    private BigDecimal total;

}