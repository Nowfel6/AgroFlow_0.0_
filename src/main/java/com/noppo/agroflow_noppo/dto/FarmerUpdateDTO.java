package com.noppo.agroflow_noppo.dto;

import lombok.Data;

@Data
public class FarmerUpdateDTO {
    // Only the fields that can be edited from the profile page
    private String name;
    private String contact;
    private String nid;
    private String district;
    private String union_name;
    private String village;
}