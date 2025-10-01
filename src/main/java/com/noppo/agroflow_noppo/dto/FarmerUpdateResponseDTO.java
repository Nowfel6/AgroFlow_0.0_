package com.noppo.agroflow_noppo.dto;

import lombok.Data;

@Data
public class FarmerUpdateResponseDTO {
    // Only the fields that might have changed and are needed by the frontend
    private Integer id;
    private String name;
    private String contact;
    private String nid;
    private String district;
    private String union_name;
    private String village;
}