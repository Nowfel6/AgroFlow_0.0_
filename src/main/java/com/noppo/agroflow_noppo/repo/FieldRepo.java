package com.noppo.agroflow_noppo.repo;

import com.noppo.agroflow_noppo.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepo extends JpaRepository<Field, Integer> {
    List<Field> findAllByFarmerId(Integer farmerId);
    @Query("SELECT f FROM Field f WHERE f.farmer.officer.id = :officerId AND NOT EXISTS " +
            "(SELECT 1 FROM CropPlan cp WHERE cp.field.id = f.id AND cp.status = 'Ongoing')")
    List<Field> findFieldsWithNoOngoingCropPlan(@Param("officerId") Integer officerId);
}