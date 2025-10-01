package com.noppo.agroflow_noppo.service;
import com.noppo.agroflow_noppo.dto.LoginRequest;
import com.noppo.agroflow_noppo.dto.LoginResponseDTO;
import com.noppo.agroflow_noppo.dto.OfficerProfileDTO;
import com.noppo.agroflow_noppo.dto.ProfileUpdateDTO;
import com.noppo.agroflow_noppo.model.KrishiOfficer;
import com.noppo.agroflow_noppo.repo.OfficerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OfficerService {
    @Autowired
   private OfficerRepo repo;

    public KrishiOfficer addOfficer(KrishiOfficer officer) {
        // --- BACKEND VALIDATION LOGIC ---

        // 1. Validate Name: Check for numbers
        if (officer.getName() == null || !officer.getName().matches("[a-zA-Z\\s.-]+")) {
            throw new IllegalArgumentException("Name cannot contain numbers or special characters.");
        }

        // 2. Validate Contact Number: Must be 11 digits
        if (officer.getContact() == null || !officer.getContact().matches("^\\d{11}$")) {
            throw new IllegalArgumentException("Contact number must be exactly 11 digits.");
        }

        // 3. Validate NID: Must be 10, 13, or 17 digits
        if (officer.getNid() == null || !officer.getNid().matches("^(\\d{10}|\\d{13}|\\d{17})$")) {
            throw new IllegalArgumentException("National ID (NID) must be 10, 13, or 17 digits.");
        }

        // 4. Check if email already exists
        if (repo.findByEmail(officer.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use. Please use a different email.");
        }

        // If all validation passes, then save the officer.
        return repo.save(officer);
    }

    public LoginResponseDTO loginOfficer(LoginRequest loginRequest) {
        // 1. Find the officer by the provided email.
        Optional<KrishiOfficer> officerOptional = repo.findByEmail(loginRequest.getEmail());

        // 2. Check if the officer exists.
        if (officerOptional.isEmpty()) {
            throw new IllegalStateException("Invalid email or password");
        }

        KrishiOfficer officer = officerOptional.get();

        // 3. Compare the plain text passwords.
        if (loginRequest.getPassword().equals(officer.getPassword())) {
            // --- SUCCESS ---
            // 4. Create a new LoginResponseDTO and populate it with the required data.
            LoginResponseDTO responseDTO = new LoginResponseDTO();
            responseDTO.setId(officer.getId());
            responseDTO.setEmail(officer.getEmail());
            responseDTO.setPassword(officer.getPassword()); // As requested
            responseDTO.setName(officer.getName()); // Also including name is very helpful

            // 5. Return the DTO.
            return responseDTO;
        } else {
            // --- FAILURE ---
            // If passwords do not match, throw the error.
            throw new IllegalStateException("Invalid email or password");
        }
    }

    // --- NEW METHOD: Get officer details by ID ---
    // Change the return type of this method
    public OfficerProfileDTO getOfficerById(Integer officerId) {
        // 1. Fetch the full entity
        KrishiOfficer officer = repo.findById(officerId)
                .orElseThrow(() -> new EntityNotFoundException("Officer not found with ID: " + officerId));

        // 2. Map the entity to the DTO
        OfficerProfileDTO dto = new OfficerProfileDTO();
        dto.setId(officer.getId());
        dto.setName(officer.getName());
        dto.setContact(officer.getContact());
        dto.setEmail(officer.getEmail());
        dto.setNid(officer.getNid());
        dto.setOffice_location(officer.getOffice_location());

        // 3. Return the safe, flat DTO
        return dto;
    }
    // --- NEW METHOD: Update officer profile ---
    @Transactional
// --- CHANGE THE RETURN TYPE HERE ---
    public OfficerProfileDTO updateOfficerProfile(Integer officerId, ProfileUpdateDTO updateDTO) {
        // 1. Find the existing officer in the database.
        KrishiOfficer officerToUpdate = repo.findById(officerId)
                .orElseThrow(() -> new EntityNotFoundException("Officer not found with ID: " + officerId));

        // 2. Update the fields from the DTO.
        officerToUpdate.setName(updateDTO.getName());
        officerToUpdate.setContact(updateDTO.getContact());
        officerToUpdate.setOffice_location(updateDTO.getOffice_location());

        // 3. Save the updated entity.
        KrishiOfficer savedOfficer = repo.save(officerToUpdate);

        // --- 4. MAP THE SAVED ENTITY TO A DTO BEFORE RETURNING ---
        OfficerProfileDTO responseDTO = new OfficerProfileDTO();
        responseDTO.setId(savedOfficer.getId());
        responseDTO.setName(savedOfficer.getName());
        responseDTO.setContact(savedOfficer.getContact());
        responseDTO.setEmail(savedOfficer.getEmail());
        responseDTO.setNid(savedOfficer.getNid());
        responseDTO.setOffice_location(savedOfficer.getOffice_location());

        return responseDTO;
    }
}
