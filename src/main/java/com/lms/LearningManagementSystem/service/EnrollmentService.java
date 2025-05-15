package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Enrollment;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import com.lms.LearningManagementSystem.repository.EnrollmentRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import com.lms.LearningManagementSystem.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private NotificationService notificationService;

    // Admin can view all enrollments
    @PreAuthorize("hasRole('ADMIN')")
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // Students can view their own enrollments, instructors can view enrollments for their courses
    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id or " +
                  "hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfAnyEnrolledCourse(authentication.principal, #studentId) or " +
                  "hasRole('ADMIN')")
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    // Instructors can view enrollments for their courses, admin can view any
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId) or " +
                  "hasRole('ADMIN')")
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    // Only students can enroll themselves in courses
    @PreAuthorize("#username == authentication.name")
    public Enrollment enrollStudentInCourse(String username, Long courseId) {
        User student = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        long studentId = student.getId();
        if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        course.getEnrolledStudents().add(username);
        student.getEnrolledCourses().add(course);

        userRepository.save(student);
        courseRepository.save(course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        String subject = "Course Enrollment";
        // Send notification to the student (enrollment confirmation)
        String studentMessage = "You have been successfully enrolled in the course: " + course.getTitle();

        // notif part
        User instructor = course.getInstructor();
        String instructorMessage = "New student " + student.getUsername() + " has enrolled in your course: " + course.getTitle();
        try {
            notificationService.createNotification(student, subject, studentMessage);
            notificationService.createNotification(instructor, subject, instructorMessage);
        } catch (MailException ex) {
            return savedEnrollment;
        }

        return savedEnrollment;
    }

    // Instructors can remove students from their courses, admin can remove from any course
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId) or " +
                  "hasRole('ADMIN')")
    public void unenrollStudent(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);

        // maintain consistency in db
        User student = enrollment.getStudent();
        Course course = enrollment.getCourse();

        student.getEnrolledCourses().remove(course);
        course.getEnrolledStudents().remove(student.getUsername());

        userRepository.save(student);
        courseRepository.save(course);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getEnrolledCourses(String username) {
        return courseRepository.findByEnrolledStudentsContaining(username);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId) or hasRole('ADMIN')")
    public List<String> getEnrolledStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return new ArrayList<>(course.getEnrolledStudents());
    }
}
