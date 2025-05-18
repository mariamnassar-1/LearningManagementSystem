//package com.lms.LearningManagementSystem.service;
//
//import com.lms.LearningManagementSystem.model.Performance;
//import com.lms.LearningManagementSystem.model.Enrollment;
//import com.lms.LearningManagementSystem.repository.PerformanceRepository;
//import com.lms.LearningManagementSystem.repository.EnrollmentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class PerformanceService {
//
//    @Autowired
//    private PerformanceRepository performanceRepository;
//
//    @Autowired
//    private EnrollmentRepository enrollmentRepository;
//
//    // Admin or Instructor can view all performance records
//    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
//    public List<Performance> getAllPerformances() {
//        return performanceRepository.findAll();
//    }
//
//    // Students can view their own performance records
//    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id")
//    public List<Performance> getPerformanceByStudent(Long studentId) {
//        return performanceRepository.findByEnrollmentId(studentId);
//    }
//
//    // Instructors can view performance for specific enrollments in their courses
//    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #enrollmentId) or " +
//            "hasRole('ADMIN')")
//    public List<Performance> getPerformanceByEnrollment(Long enrollmentId) {
//        return performanceRepository.findByEnrollmentId(enrollmentId);
//    }
//
//    // Instructors can mark attendance or update quiz and assignment scores
//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    public Performance updatePerformance(Long enrollmentId, Double quizScore, Double assignmentScore, boolean attendanceMarked) {
//        // Fetch enrollment linked to the performance
//        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
//
//        // Check if performance already exists, otherwise create a new record
//        Performance performance = performanceRepository.findByEnrollmentId(enrollmentId).stream()
//                .findFirst()
//                .orElse(new Performance());
//
//        performance.setEnrollment(enrollment);
//        performance.setQuizScore(quizScore);
//        performance.setAssignmentScore(assignmentScore);
//        performance.setAttendanceMarked(attendanceMarked);
//
//        return performanceRepository.save(performance);
//    }
//
//    // Admin or Instructor can delete performance records
//    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
//    public void deletePerformance(Long performanceId) {
//        Performance performance = performanceRepository.findById(performanceId)
//                .orElseThrow(() -> new IllegalArgumentException("Performance record not found"));
//        performanceRepository.delete(performance);
//    }
//}
