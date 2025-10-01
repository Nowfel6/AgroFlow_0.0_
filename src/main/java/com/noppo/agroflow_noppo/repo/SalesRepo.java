package com.noppo.agroflow_noppo.repo;

import com.noppo.agroflow_noppo.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SalesRepo extends JpaRepository<Sales, Integer> {
    @Query("SELECT s FROM Sales s WHERE s.saleRequest.farmer.id = :farmerId")
    List<Sales> findAllByFarmerId(@Param("farmerId") Integer farmerId);


    @Query("SELECT SUM(s.final_quantity) FROM Sales s WHERE s.saleRequest.officer.id = :officerId AND s.saleRequest.harvest.cropPlan.crop.id = :cropId AND FUNCTION('YEAR', s.sale_date) = :year")
    BigDecimal sumTotalYieldByOfficerAndCropAndYear(@Param("officerId") Integer officerId, @Param("cropId") Integer cropId, @Param("year") int year);

    @Query("SELECT SUM(s.saleRequest.harvest.cropPlan.field.size) FROM Sales s WHERE s.saleRequest.officer.id = :officerId AND s.saleRequest.harvest.cropPlan.crop.id = :cropId AND FUNCTION('YEAR', s.sale_date) = :year")
    Double sumTotalAreaByOfficerAndCropAndYear(@Param("officerId") Integer officerId, @Param("cropId") Integer cropId, @Param("year") int year);

    @Query("SELECT COUNT(DISTINCT s.saleRequest.farmer.id) FROM Sales s WHERE s.saleRequest.officer.id = :officerId AND s.saleRequest.harvest.cropPlan.crop.id = :cropId AND FUNCTION('YEAR', s.sale_date) = :year")
    Long countDistinctFarmersByOfficerAndCropAndYear(@Param("officerId") Integer officerId, @Param("cropId") Integer cropId, @Param("year") int year);
    // ... inside SalesRepository.java
    @Query("SELECT COUNT(DISTINCT s.saleRequest.farmer.id) FROM Sales s WHERE FUNCTION('YEAR', s.sale_date) = :year")
    Long countDistinctFarmersByYear(@Param("year") int year);
}