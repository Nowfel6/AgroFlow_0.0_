package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.dto.AnalyticsDTO;
import com.noppo.agroflow_noppo.dto.CropSearchResultDTO;
import com.noppo.agroflow_noppo.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/officer/{officerId}")
    public ResponseEntity<AnalyticsDTO> getAnalytics(@PathVariable Integer officerId) {
        AnalyticsDTO data = analyticsService.getAnalyticsForOfficer(officerId);
        return ResponseEntity.ok(data);
    }
    @GetMapping("/search-crop")
    public ResponseEntity<List<CropSearchResultDTO>> searchByCrop(
            @RequestParam Integer officerId,
            @RequestParam String cropName) {

        List<CropSearchResultDTO> results = analyticsService.searchOngoingPlansByCrop(officerId, cropName);
        return ResponseEntity.ok(results);
    }
}