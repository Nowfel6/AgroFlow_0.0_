package com.noppo.agroflow_noppo.dto;

import com.noppo.agroflow_noppo.model.MarketPrice;
import com.noppo.agroflow_noppo.model.SaleRequest;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleRequestDetailsDTO {
    // Basic Request Info
    private Integer id;
    private String farmerName;
    private String cropName;
    private BigDecimal quantity_requested;
    private BigDecimal price_per_kg_requested;
    private String delivery_status;

    // Payment Info
    private String payment_mode_requested;
    private String bank_name;
    private String account_holder_name;
    private String account_number;
    private String ifsc_code;
    private String online_banking_no;

    // Market Price Info
    private BigDecimal currentMarketPrice; // Now it's just the price
}