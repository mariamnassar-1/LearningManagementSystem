//package com.lms.LearningManagementSystem.controller;
//
//import com.lms.LearningManagementSystem.model.Performance;
//import com.lms.LearningManagementSystem.service.PerformanceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.CrossOrigin;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/performances")
//@CrossOrigin(origins = "*")
//public class PerformanceController {
//
//    @Autowired
//    private PerformanceService performanceService;
//
//    // Admin or Instructors can view all performance records
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
//    public ResponseEntity<List<Performance>> getAllPerformances() {
//        return ResponseEntity.ok(performanceService.getAllPerformances());
//    }
//
//    // Students can view their own performance records
//    @GetMapping("/student")
//    @PreAuthorize("hasRole('STUDENT')")
//    public ResponseEntity<List<Performance>> getPerformanceByStudent(Authentication authentication) {
//        Long studentId = Long.parseLong(authentication.getName()); // Student ID from authentication
//        return ResponseEntity.ok(performanceService.getPerformanceByStudent(studentId));
//    }
//
//    // Instructors or Admins can view performance records for a specific enrollment
//    @GetMapping("/enrollment/{enrollmentId}")
//    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #enrollmentId) or " +
//            "hasRole('ADMIN')")
//    public ResponseEntity<List<Performance>> getPerformanceByEnrollment(@PathVariable Long enrollmentId) {
//        return ResponseEntity.ok(performanceService.getPerformanceByEnrollment(enrollmentId));
//    }
//
//    // Instructors can update or create a performance record
//    @PostMapping("/update")
//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    public ResponseEntity<?> updatePerformance(@RequestParam Long enrollmentId,
//                                               @RequestParam(required = false) Double quizScore,
//                                               @RequestParam(required = false) Double assignmentScore,
//                                               @RequestParam(required = false) Boolean attendanceMarked) {
//        try {
//            Performance performance = performanceService.updatePerformance(enrollmentId, quizScore, assignmentScore, attendanceMarked);
//            return ResponseEntity.ok(performance);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    // Admins or Instructors can delete a performance record
//    @DeleteMapping("/delete/{performanceId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
//    public ResponseEntity<?> deletePerformance(@PathVariable Long performanceId) {
//        try {
//            performanceService.deletePerformance(performanceId);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//}
