package com.noppo.agroflow_noppo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FieldDTO {
    private int id;
    private String name;
    private String landmark;
    private String soil_type;
    private String irrigation_type;
    private BigDecimal size;
    private String ownership_type;
}
