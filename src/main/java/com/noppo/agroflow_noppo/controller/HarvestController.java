package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.model.Harvest;
import com.noppo.agroflow_noppo.service.HarvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/harvests")
@CrossOrigin(origins = "*")
public class HarvestController {

    @Autowired
    private HarvestService harvestService;

    // Endpoint will be POST /api/harvests/record?cropPlanId=123
    @PostMapping("/record")
    public ResponseEntity<?> recordHarvest(@RequestBody Harvest harvest, @RequestParam Integer cropPlanId) {
        try {
            Harvest newHarvest = harvestService.recordHarvest(harvest, cropPlanId);
            return new ResponseEntity<>(newHarvest, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}