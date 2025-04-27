package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.Enrollment;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import com.lms.LearningManagementSystem.repository.EnrollmentRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSecurityService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public boolean isInstructorOfCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getInstructor() != null && 
               course.getInstructor().getId() == user.getId();
    }

    public boolean isEnrolledInCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getEnrolledStudents().contains(user.getUsername());
    }

    public boolean isInstructorOfAnyEnrolledCourse(String username, Long studentId) {
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        // Get all enrollments for the student
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(studentId);

        // Check if the instructor teaches any of the courses the student is enrolled in
        return studentEnrollments.stream()
                .map(Enrollment::getCourse)
                .anyMatch(course -> course.getInstructor() != null &&
                        course.getInstructor().getId() == instructor.getId());
    }
}
