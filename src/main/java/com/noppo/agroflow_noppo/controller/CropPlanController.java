package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.model.CropPlan;
import com.noppo.agroflow_noppo.service.CropPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cropplans")
@CrossOrigin(origins = "*")
public class CropPlanController {

    @Autowired
    private CropPlanService cropPlanService;

    @PostMapping("/add")
    public ResponseEntity<?> addCropPlan(@RequestBody CropPlan cropPlan) {
        try {
            CropPlan newPlan = cropPlanService.createCropPlan(cropPlan);
            return new ResponseEntity<>(newPlan, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}