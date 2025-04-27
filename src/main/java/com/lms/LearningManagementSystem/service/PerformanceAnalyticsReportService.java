package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Performance;
import com.lms.LearningManagementSystem.repository.PerformanceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PerformanceAnalyticsReportService {

    @Autowired
    private PerformanceRepository performanceRepository;

    // Method to generate an Excel report for student performance
    public byte[] generatePerformanceReport(Long courseId) throws IOException {
        // Retrieve performance data for a specific course
        List<Performance> performances = performanceRepository.findByEnrollmentId(courseId);

        // Create a workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Performance Report"); // Create a sheet

        // Create the header row
        Row headerRow = sheet.createRow(0); // First row is for headers
        headerRow.createCell(0).setCellValue("Student ID");
        headerRow.createCell(1).setCellValue("Quiz Score");
        headerRow.createCell(2).setCellValue("Assignment Grade");
        headerRow.createCell(3).setCellValue("Attendance");

        // Fill in data rows
        int rowNum = 1;
        for (Performance performance : performances) {
            Row row = sheet.createRow(rowNum++); // Create a new row for each student
            row.createCell(0).setCellValue(performance.getEnrollment().getStudent().getId()); // Student ID
            row.createCell(1).setCellValue(performance.getQuizScore()); // Quiz score
            row.createCell(2).setCellValue(performance.getAssignmentGrade()); // Assignment grade
            row.createCell(3).setCellValue(performance.isAttendanceMarked() ? "Present" : "Absent"); // Attendance
        }

        // Generate a downloadable file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray(); // Return byte array of the Excel file
    }
}
