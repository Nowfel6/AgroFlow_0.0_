package com.noppo.agroflow_noppo.controller;

import com.noppo.agroflow_noppo.dto.ReportsDTO;
import com.noppo.agroflow_noppo.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/generate")
    public ResponseEntity<ReportsDTO> getReportData(
            @RequestParam Integer officerId,
            @RequestParam Integer cropId,
            @RequestParam String sessionYear) {
        ReportsDTO report = reportsService.getOrCreateReport(officerId, cropId, sessionYear);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/update")
    public ResponseEntity<ReportsDTO> updateReport(@RequestBody ReportsDTO reportDTO) {
        ReportsDTO updatedReport = reportsService.saveOrUpdateReport(reportDTO);
        return ResponseEntity.ok(updatedReport);
    }
}