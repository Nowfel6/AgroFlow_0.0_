package com.noppo.agroflow_noppo.dto;

import lombok.Data;

@Data
public class FarmerListDTO {
    private Integer id;
    private String name;
    private String contact;
    private String village;
    // We do NOT include the officer object or any lists here.
}