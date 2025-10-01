package com.noppo.agroflow_noppo.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Integer id; // Or Long if you changed your ID type
    private String email;
    private String password;
    private String name; // It's very useful to also send the name for display purposes
}