package com.noppo.agroflow_noppo.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UnusedFieldDTO {
    private String farmerName;
    private String fieldName;
    private BigDecimal size;
}