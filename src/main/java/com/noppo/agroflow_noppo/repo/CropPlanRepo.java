package com.noppo.agroflow_noppo.repo;

import com.noppo.agroflow_noppo.dto.HarvestSummaryDTO;
import com.noppo.agroflow_noppo.model.CropPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CropPlanRepo extends JpaRepository<CropPlan, Integer> {
    List<CropPlan> findAllByFarmerId(Integer farmerId);

    @Query("SELECT count(cp) FROM CropPlan cp WHERE cp.farmer.officer.id = :officerId AND cp.status = :status AND YEAR(cp.plantingDate) = :year")
    long countByOfficerIdAndStatusAndYear(@Param("officerId") Integer officerId, @Param("status") String status, @Param("year") int year);

    List<CropPlan> findAllByFarmer_Officer_IdAndStatusOrderByExpectedHarvestDateAsc(Integer officerId, String status);
    // 3. The associated Crop's name contains the search term (case-insensitive).
    List<CropPlan> findAllByFarmer_Officer_IdAndStatusAndCrop_NameContainingIgnoreCase(
            Integer officerId,
            String status,
            String cropName
    );
    @Query("SELECT SUM(f.size) FROM Field f WHERE f.id IN " +
            "(SELECT DISTINCT cp.field.id FROM CropPlan cp WHERE cp.status = 'Harvested' AND cp.plantingDate BETWEEN :startDate AND :endDate)")
    Double sumAreaOfDistinctFieldsByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}