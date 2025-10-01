package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.model.Farmer;
import com.noppo.agroflow_noppo.model.Field;
import com.noppo.agroflow_noppo.repo.FarmerRepo;
import com.noppo.agroflow_noppo.repo.FieldRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {

    @Autowired
    private FieldRepo fieldRepository;

    @Autowired
    private FarmerRepo farmerRepository;

    public Field addNewField(Field field, Integer farmerId) {
        // 1. Find the farmer to whom this field will belong.
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot add field: Farmer not found with ID " + farmerId));

        // 2. Set the relationship: link the new field to the farmer.
        field.setFarmer(farmer);

        // 3. Save the new field. JPA will automatically handle the foreign key.
        return fieldRepository.save(field);
    }

    public List<Field> getFieldsForFarmer(Integer farmerId) {
        List<Field> fields= fieldRepository.findAllByFarmerId(farmerId);
        return  fields;
    }
}