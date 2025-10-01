package com.noppo.agroflow_noppo.repo;

import com.noppo.agroflow_noppo.model.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceRepo extends JpaRepository<MarketPrice, Integer> {
    List<MarketPrice> findAllByCropId(Integer cropId);
//    Optional<MarketPrice> findTopByCropIdOrderByEffective_monthDesc(Integer cropId);
}