package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.dto.*;
import com.noppo.agroflow_noppo.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advanced-reports")
@CrossOrigin(origins = "*")
public class AdvancedReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/summary-cards")
    public ResponseEntity<AdvancedReportDTO> getSummaryCards(@RequestParam String year) {
        return ResponseEntity.ok(reportsService.getSummaryCardData(year));
    }

    @GetMapping("/main-report")
    public ResponseEntity<List<MainReportRowDTO>> getMainReport(@RequestParam String year, @RequestParam String region) {
        return ResponseEntity.ok(reportsService.getMainReport(year, region));
    }

    @GetMapping("/top-regions")
    public ResponseEntity<List<TopRegionDTO>> getTopRegions(@RequestParam String year, @RequestParam Integer cropId) {
        return ResponseEntity.ok(reportsService.getTopRegionsReport(year, cropId));
    }

    @GetMapping("/chart-data")
    public ResponseEntity<List<ChartDataDTO>> getChartData(@RequestParam Integer cropId) {
        return ResponseEntity.ok(reportsService.getChartData(cropId));
    }

    @GetMapping("/filters")
    public ResponseEntity<FilterDataDTO> getFilters() {
        return ResponseEntity.ok(reportsService.getFilterData());
    }
}