package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.dto.FarmerListDTO;
import com.noppo.agroflow_noppo.dto.FarmerUpdateDTO;
import com.noppo.agroflow_noppo.dto.FarmerUpdateResponseDTO;
import com.noppo.agroflow_noppo.model.Farmer;
import com.noppo.agroflow_noppo.model.KrishiOfficer;
import com.noppo.agroflow_noppo.repo.FarmerRepo;
import com.noppo.agroflow_noppo.repo.OfficerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Farmer_RegisterService {
    @Autowired
    private FarmerRepo farmerRepository;

    @Autowired
    private OfficerRepo officerRepository; // You need this to verify the officer exists

    public Farmer registerFarmer(Farmer farmer) {
        // Important: The incoming 'farmer' object has an 'officer' with only an ID.
        // We need to make sure this officer actually exists in the database.
        KrishiOfficer officer = officerRepository.findById(farmer.getOfficer().getId())
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + farmer.getOfficer().getId()));

        // Now that we have the full officer object, set it on the farmer
        farmer.setOfficer(officer);

        // Save the new farmer to the database
        return farmerRepository.save(farmer);
    }
    // --- NEW METHOD 1: Get all farmers for an officer ---
    @Transactional(readOnly = true) // Add Transactional for safety
    public List<FarmerListDTO> getFarmersByOfficerId(Integer officerId) {
        // 1. Fetch the list of Farmer entities
        List<Farmer> farmers = farmerRepository.findAllByOfficerId(officerId);

        // 2. Map the list of entities to a list of DTOs
        return farmers.stream().map(farmer -> {
            FarmerListDTO dto = new FarmerListDTO();
            dto.setId(farmer.getId());
            dto.setName(farmer.getName());
            dto.setContact(farmer.getContact());
            dto.setVillage(farmer.getVillage());
            return dto;
        }).collect(Collectors.toList());
    }

    // --- NEW METHOD 2: Search farmers for an officer ---
    @Transactional(readOnly = true)
    public List<FarmerListDTO> searchFarmersByNameForOfficer(Integer officerId, String name) {
        // 1. Fetch the list of Farmer ENTITIES from the repository
        List<Farmer> farmers = farmerRepository.findByOfficerIdAndNameContainingIgnoreCase(officerId, name);

        // 2. MANUALLY map the entities to DTOs before returning
        return mapFarmerListToDTO(farmers); // Use the same helper method
    }

    // --- HELPER METHOD to avoid repeating code ---
    private List<FarmerListDTO> mapFarmerListToDTO(List<Farmer> farmers) {
        return farmers.stream().map(farmer -> {
            FarmerListDTO dto = new FarmerListDTO();
            dto.setId(farmer.getId());
            dto.setName(farmer.getName());
            dto.setContact(farmer.getContact());
            dto.setVillage(farmer.getVillage());
            return dto;
        }).collect(Collectors.toList());
    }
    // --- NEW METHOD: Update an existing farmer's profile ---
    @Transactional
    public FarmerUpdateResponseDTO updateFarmerProfile(Integer farmerId, FarmerUpdateDTO updateDTO) {
        // 1. Find the existing farmer in the database.
        Farmer farmerToUpdate = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new EntityNotFoundException("Farmer not found with ID: " + farmerId));

        // 2. Update the fields from the DTO.
        farmerToUpdate.setName(updateDTO.getName());
        farmerToUpdate.setContact(updateDTO.getContact());
        farmerToUpdate.setNid(updateDTO.getNid());
        farmerToUpdate.setDistrict(updateDTO.getDistrict());
        farmerToUpdate.setUnion_name(updateDTO.getUnion_name());
        farmerToUpdate.setVillage(updateDTO.getVillage());

        // The 'officer' relationship remains unchanged.

        // 3. Save the updated entity. JPA performs an UPDATE statement.
        Farmer savedFarmer=farmerRepository.save(farmerToUpdate);
        FarmerUpdateResponseDTO responseDTO = new FarmerUpdateResponseDTO();
        responseDTO.setId(savedFarmer.getId());
        responseDTO.setName(savedFarmer.getName());
        responseDTO.setContact(savedFarmer.getContact());
        responseDTO.setNid(savedFarmer.getNid());
        responseDTO.setDistrict(savedFarmer.getDistrict());
        responseDTO.setUnion_name(savedFarmer.getUnion_name());
        responseDTO.setVillage(savedFarmer.getVillage());

        return responseDTO;
    }
}
