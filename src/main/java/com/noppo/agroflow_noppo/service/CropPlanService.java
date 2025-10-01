package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.model.Crop;
import com.noppo.agroflow_noppo.model.CropPlan;
import com.noppo.agroflow_noppo.model.Farmer;
import com.noppo.agroflow_noppo.model.Field;
import com.noppo.agroflow_noppo.repo.*; // Import all your repos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CropPlanService {

    @Autowired private CropPlanRepo cropPlanRepository;
    @Autowired private FarmerRepo farmerRepository;
    @Autowired private CropRepo cropRepository;
    @Autowired private FieldRepo fieldRepository;

    public CropPlan createCropPlan(CropPlan cropPlan) {
        // The incoming cropPlan object has Farmer, Crop, and Field objects with only their IDs set.
        // We need to fetch the full, managed entities from the database.

        Farmer farmer = farmerRepository.findById(cropPlan.getFarmer().getId())
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Crop crop = cropRepository.findById(cropPlan.getCrop().getId())
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        Field field = fieldRepository.findById(cropPlan.getField().getId())
                .orElseThrow(() -> new RuntimeException("Field not found"));

        // Set the full objects on the new crop plan
        cropPlan.setFarmer(farmer);
        cropPlan.setCrop(crop);
        cropPlan.setField(field);

        // You would add your date overlap validation logic here!

        return cropPlanRepository.save(cropPlan);
    }
}