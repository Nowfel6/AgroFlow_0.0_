package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.model.Crop;
import com.noppo.agroflow_noppo.repo.CropRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = "*")
public class CropController {

    @Autowired
    private CropRepo cropRepository;

    @GetMapping
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropRepository.findAll());
    }
}