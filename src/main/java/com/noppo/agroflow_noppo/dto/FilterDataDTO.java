package com.noppo.agroflow_noppo.dto;

import com.noppo.agroflow_noppo.model.Crop; // You can use the entity here or a simpler CropDTO
import lombok.Data;

import java.util.List;

@Data
public class FilterDataDTO {

    private List<String> sessionYears;
    private List<String> regions;
    private List<Crop> crops; // Or List<CropDTO> if you create one

}