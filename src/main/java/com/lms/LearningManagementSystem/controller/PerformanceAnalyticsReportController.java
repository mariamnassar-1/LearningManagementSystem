package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.service.PerformanceAnalyticsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/analytics")
public class PerformanceAnalyticsReportController {

    @Autowired
    private PerformanceAnalyticsReportService performanceAnalyticsReportService;

    // Endpoint to generate and download the performance report in Excel format
    @GetMapping("/performance-report/{courseId}")
    public ResponseEntity<byte[]> generatePerformanceReport(@PathVariable Long courseId) throws IOException {
        byte[] report = performanceAnalyticsReportService.generatePerformanceReport(courseId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=performance_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }
}
