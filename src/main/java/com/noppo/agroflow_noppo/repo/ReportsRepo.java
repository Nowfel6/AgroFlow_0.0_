package com.noppo.agroflow_noppo.repo;

import com.noppo.agroflow_noppo.dto.MainReportRowDTO;
import com.noppo.agroflow_noppo.dto.TopRegionDTO;
import com.noppo.agroflow_noppo.model.Reports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportsRepo extends JpaRepository<Reports, Integer> {
    Optional<Reports> findByOfficer_IdAndCrop_IdAndSessionYear(Integer officerId, Integer cropId, String sessionYear);
    // --- QUERIES FOR SECTION 1: SUMMARY CARDS ---
    @Query("SELECT SUM(r.no_of_farmer) FROM Reports r WHERE r.sessionYear = :year")
    Long sumTotalFarmersByYear(@Param("year") String year);

    @Query("SELECT SUM(r.total_area_cultivated) FROM Reports r WHERE r.sessionYear = :year")
    Double sumTotalAreaByYear(@Param("year") String year);

    @Query("SELECT SUM(r.total_yield) FROM Reports r WHERE r.sessionYear = :year")
    BigDecimal sumTotalHarvestByYear(@Param("year") String year);

    @Query("SELECT r.crop.name FROM Reports r WHERE r.sessionYear = :year GROUP BY r.crop.name ORDER BY SUM(r.total_yield) DESC LIMIT 1")
    String findTopCropByYear(@Param("year") String year);

    // --- QUERY FOR SECTION 2: MAIN REPORT TABLE ---
    @Query("SELECT new com.noppo.agroflow_noppo.dto.MainReportRowDTO(r.crop.name, SUM(r.total_yield), SUM(r.total_area_cultivated), SUM(r.no_of_farmer)) " +
            "FROM Reports r WHERE r.sessionYear = :year " +
            "GROUP BY r.crop.name")
    List<MainReportRowDTO> getNationalReportByYear(@Param("year") String year);

    @Query("SELECT new com.noppo.agroflow_noppo.dto.MainReportRowDTO(r.crop.name, SUM(r.total_yield), SUM(r.total_area_cultivated), SUM(r.no_of_farmer)) " +
            "FROM Reports r WHERE r.sessionYear = :year AND r.region = :region " +
            "GROUP BY r.crop.name")
    List<MainReportRowDTO> getRegionalReportByYear(@Param("year") String year, @Param("region") String region);

    // --- QUERY FOR SECTION 3: TOP REGIONS TABLE ---
    @Query("SELECT new com.noppo.agroflow_noppo.dto.TopRegionDTO(r.region, SUM(r.total_yield)) " +
            "FROM Reports r WHERE r.sessionYear = :year AND r.crop.id = :cropId " +
            "GROUP BY r.region ORDER BY SUM(r.total_yield) DESC LIMIT 10")
    List<TopRegionDTO> getTopRegionsForCropByYear(@Param("year") String year, @Param("cropId") Integer cropId);

    // --- QUERY FOR SECTION 4: 3-YEAR CHART ---
    @Query("SELECT SUM(r.total_yield) FROM Reports r WHERE r.crop.id = :cropId AND r.sessionYear = :year")
    BigDecimal getNationalHarvestForCropByYear(@Param("cropId") Integer cropId, @Param("year") String year);

    // --- Methods to populate filters ---
    @Query("SELECT DISTINCT r.sessionYear FROM Reports r ORDER BY r.sessionYear DESC")
    List<String> findDistinctSessionYears();

    @Query("SELECT DISTINCT r.region FROM Reports r ORDER BY r.region ASC")
    List<String> findDistinctRegions();
}