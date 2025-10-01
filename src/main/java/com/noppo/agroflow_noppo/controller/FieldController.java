package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.model.Field;
import com.noppo.agroflow_noppo.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = "*")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    // The endpoint will be POST /api/fields/add?farmerId=123
    @PostMapping("/add")
    public ResponseEntity<?> addField(@RequestBody Field field, @RequestParam Integer farmerId) {
        try {
            Field newField = fieldService.addNewField(field, farmerId);
            return new ResponseEntity<>(newField, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    // ... inside FieldController.java
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<Field>> getFieldsByFarmer(@PathVariable Integer farmerId) {
        // You would need to create this service method
        List<Field> fields = fieldService.getFieldsForFarmer(farmerId);
        return ResponseEntity.ok(fields);
    }
}