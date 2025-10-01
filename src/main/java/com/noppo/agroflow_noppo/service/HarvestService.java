package com.noppo.agroflow_noppo.service;

import com.noppo.agroflow_noppo.model.CropPlan;
import com.noppo.agroflow_noppo.model.Harvest;
import com.noppo.agroflow_noppo.repo.CropPlanRepo;
import com.noppo.agroflow_noppo.repo.HarvestRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // IMPORTANT

@Service
public class HarvestService {

    @Autowired
    private HarvestRepo harvestRepository;

    @Autowired
    private CropPlanRepo cropPlanRepository;

    @Transactional // This makes the entire method a single database transaction
    public Harvest recordHarvest(Harvest harvest, Integer cropPlanId) {
        // 1. Find the CropPlan that this harvest is for.
        CropPlan cropPlan = cropPlanRepository.findById(cropPlanId)
                .orElseThrow(() -> new EntityNotFoundException("CropPlan not found with ID: " + cropPlanId));

        // 2. Check if a harvest has already been recorded for this plan.
        if (cropPlan.getHarvest() != null) {
            throw new IllegalStateException("A harvest has already been recorded for this crop plan.");
        }

        // 3. Update the CropPlan's status.
        cropPlan.setStatus("Harvested");

        // 4. Set the relationship: link the new harvest to its crop plan.
        harvest.setCropPlan(cropPlan);

        // 5. Save the new Harvest. Because of the @OneToOne relationship and cascading,
        //    saving the harvest might be enough, but saving both is explicit and safe.
        Harvest savedHarvest = harvestRepository.save(harvest);

        // 6. Save the updated CropPlan.
        cropPlanRepository.save(cropPlan);

        return savedHarvest;
    }
}