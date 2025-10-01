package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.dto.LoginRequest;
import com.noppo.agroflow_noppo.dto.LoginResponseDTO;
import com.noppo.agroflow_noppo.dto.OfficerProfileDTO;
import com.noppo.agroflow_noppo.dto.ProfileUpdateDTO;
import com.noppo.agroflow_noppo.model.KrishiOfficer;
import com.noppo.agroflow_noppo.service.OfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/officers")
@CrossOrigin(origins = "*")
public class OfficerController {
    @Autowired
   private OfficerService service;

   @PostMapping("/signup")
    public ResponseEntity<?> addOfficer(@RequestBody KrishiOfficer officer){
       try {
           KrishiOfficer nwofficer = service.addOfficer(officer);
           return new ResponseEntity<>(nwofficer, HttpStatus.CREATED); // 201 Created for success
       }
       // Catch the specific validation error
       catch (IllegalArgumentException | IllegalStateException e) {
           // Return 400 Bad Request with the specific error message from the service
           return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
       // A general catch-all for other unexpected errors
       catch (Exception e){
           return new ResponseEntity<>("An unexpected error occurred on the server.", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginOfficer(@RequestBody LoginRequest loginRequest){
        try {
            LoginResponseDTO officer = service.loginOfficer(loginRequest);

            // On successful login, return the officer's data.
            // It's a good practice to never send the password back to the client.
            officer.setPassword(null);

            return ResponseEntity.ok(officer);
        } catch (IllegalStateException e) {
            // This catches the "Invalid email or password" error from the service.
            // 401 Unauthorized is the correct HTTP status for a failed login.
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    // --- NEW ENDPOINT: Get officer details ---
    @GetMapping("/{officerId}")
    public ResponseEntity<OfficerProfileDTO> getOfficerDetails(@PathVariable Integer officerId) {
        try {
            OfficerProfileDTO officer = service.getOfficerById(officerId);
            return ResponseEntity.ok(officer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- NEW ENDPOINT: Update officer profile ---
    @PutMapping("/{officerId}/update") // Using PUT for updates is a REST standard
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer officerId,
            @RequestBody ProfileUpdateDTO updateDTO) {
        try {
            OfficerProfileDTO updatedOfficer = service.updateOfficerProfile(officerId, updateDTO);
            return ResponseEntity.ok(updatedOfficer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
