package com.noppo.agroflow_noppo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesDTO {
    private int id;
    private BigDecimal final_quantity;
    private BigDecimal final_price_per_kg;
    private LocalDate sale_date;
    private String payment_status;
    private String payment_mode;
    private String cropName;
    private String delivery_status;
}
