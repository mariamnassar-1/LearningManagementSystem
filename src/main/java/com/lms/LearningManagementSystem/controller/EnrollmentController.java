package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.Enrollment;
import com.lms.LearningManagementSystem.service.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id or " +
                  "hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfAnyEnrolledCourse(authentication.principal.username, #studentId) or " +
                  "hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enrollStudentInCourse(Authentication authentication, @RequestParam Long courseId) {
        try {
            String username = authentication.getName();
            Enrollment enrollment = enrollmentService.enrollStudentInCourse(username, courseId);
            return ResponseEntity.ok(enrollment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }


    @DeleteMapping("/unenroll")
    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id or " +
            "hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfAnyEnrolledCourse(authentication.principal.username, #studentId) or " +
            "hasRole('ADMIN')")
    public ResponseEntity<?> unenrollStudent(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            enrollmentService.unenrollStudent(studentId, courseId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getEnrolledCourses(Principal principal) {
        String username = principal.getName();
        return enrollmentService.getEnrolledCourses(username);
    }

    @GetMapping("/{courseId}/students")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public ResponseEntity<?> getEnrolledStudents(@PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(enrollmentService.getEnrolledStudents(courseId));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }
}
