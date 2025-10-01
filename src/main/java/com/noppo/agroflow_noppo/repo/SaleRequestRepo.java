package com.noppo.agroflow_noppo.repo;

import com.noppo.agroflow_noppo.model.SaleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRequestRepo extends JpaRepository<SaleRequest, Integer> {
    List<SaleRequest> findAllByFarmerId(Integer farmerId);

    List<SaleRequest> findAllByOfficerIdAndSaleIsNull(Integer officerId);
}