package com.noppo.agroflow_noppo.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    // Fields the officer is allowed to change
    private String name;
    private String contact;
    private String office_location;
    // We don't include email or nid as they are usually not user-editable
}