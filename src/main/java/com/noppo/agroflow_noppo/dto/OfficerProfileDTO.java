package com.noppo.agroflow_noppo.dto;

import lombok.Data;

@Data
public class OfficerProfileDTO {
    // Only the fields needed by the profile page
    private Integer id;
    private String name;
    private String contact;
    private String email;
    private String nid;
    private String office_location;
    // DO NOT include the List<Farmer>
}