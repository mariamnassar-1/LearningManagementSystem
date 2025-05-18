package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.service.PerformanceAnalyticsChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/analytics")
public class PerformanceAnalyticsChartController {

    @Autowired
    private PerformanceAnalyticsChartService performanceAnalyticsChartService;

    // Endpoint to generate and download the performance chart
    @GetMapping("/performance-chart/{courseId}")
    public ResponseEntity<byte[]> generatePerformanceChart(@PathVariable Long courseId) throws IOException {
        // Generate the chart as a byte array
        byte[] chart = performanceAnalyticsChartService.generatePerformanceChart(courseId);

        // Set HTTP headers to make the file downloadable
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=performance_chart.png");

        // Return the chart as a response
        return ResponseEntity.ok()
                .headers(headers)
                .body(chart);
    }
}
