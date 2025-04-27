//package com.lms.LearningManagementSystem.service;
//
//import com.lms.LearningManagementSystem.model.Performance;
//import com.lms.LearningManagementSystem.repository.PerformanceRepository;
//import org.knowm.xchart.BitmapEncoder;
//import org.knowm.xchart.XYChart;
//import org.knowm.xchart.XYChartBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.awt.*;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class PerformanceAnalyticsChartService {
//
//    @Autowired
//    private PerformanceRepository performanceRepository;
//
//    // Method to generate a performance chart (bar chart for quiz scores, assignment grades, and attendance)
//    public byte[] generatePerformanceChart(Long courseId) throws IOException {
//        // Retrieve performance data for a specific course
//        List<Performance> performances = performanceRepository.findByEnrollmentId(courseId);
//
//        // Prepare data for the chart
//        int size = performances.size();
//        double[] quizScores = new double[size];
//        double[] assignmentGrades = new double[size];
//        double[] attendance = new double[size];
//        String[] studentNames = new String[size];
//
//        for (int i = 0; i < size; i++) {
//            Performance performance = performances.get(i);
//            quizScores[i] = performance.getQuizScore();
//            assignmentGrades[i] = performance.getAssignmentGrade();
//            attendance[i] = performance.isAttendanceMarked() ? 1 : 0;
//            studentNames[i] = performance.getEnrollment().getStudent().getName();
//        }
//
//        // Create a chart
//        XYChart chart = new XYChartBuilder().width(800).height(600).title("Performance Analysis").xAxisTitle("Student").yAxisTitle("Score").build();
//
//        chart.addSeries("Quiz Scores", studentNames, quizScores);
//        chart.addSeries("Assignment Grades", studentNames, assignmentGrades);
//        chart.addSeries("Attendance", studentNames, attendance);
//
//        // Customize chart appearance
//        chart.getStyler().setSeriesLinesVisible(true).setMarkerVisible(false); // Lines visible, no markers
//        chart.getStyler().setLegendPosition(XYChart.XYLegendPosition.InsideNW);
//
//        // Convert chart to byte array (PNG format)
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        BitmapEncoder.saveBitmap(chart, baos, BitmapEncoder.BitmapFormat.PNG); // Save as PNG to byte stream
//
//        return baos.toByteArray(); // Return the byte array representing the chart as an image
//    }
//}
