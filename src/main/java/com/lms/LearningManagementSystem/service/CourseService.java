package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.Lesson;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.service.UserService;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import com.lms.LearningManagementSystem.repository.LessonRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private  NotificationService notificationService;
    @Value("${media.storage.path}") // Define a property in application.properties
    private String mediaStoragePath;

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public Course createCourse(Course course) {

        // Get the instructor from the database
        User instructor = userRepository.findByUsername(course.getInstructor().getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public Course updateCourse(Long courseId, Course course) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Preserve the original instructor
        course.setId(courseId);
        course.setInstructor(existingCourse.getInstructor());
        course.setEnrolledStudents(existingCourse.getEnrolledStudents());
        String subject = "Course Update";
        String message = "Course has been updated";
        for (String studentName : course.getEnrolledStudents()) {
            notificationService.createNotification(userService.findByUsername(studentName), subject, message);
        }
        return courseRepository.save(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found");
        }
        courseRepository.deleteById(courseId);
    }

    // Accessible by all authenticated users
    @PreAuthorize("isAuthenticated()")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Course> getCoursesByInstructor(String instructorUsername) {
        return courseRepository.findByInstructorUsername(instructorUsername);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getEnrolledCourses(String username) {
        return courseRepository.findByEnrolledStudentsContaining(username);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public void enrollStudent(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (course.getEnrolledStudents().contains(username)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        course.getEnrolledStudents().add(username);
        courseRepository.save(course);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public void unenrollStudent(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!course.getEnrolledStudents().contains(username)) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }

        course.getEnrolledStudents().remove(username);
        courseRepository.save(course);
    }

    public Course uploadMediaFiles(Long courseId, List<MultipartFile> files) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String filePath = saveFile(file);
            filePaths.add(filePath);
        }

        course.getMediaFiles().addAll(filePaths);
        return courseRepository.save(course);
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(mediaStoragePath, fileName);
            Files.createDirectories(filePath.getParent()); // Ensure directory exists
            Files.write(filePath, file.getBytes());
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId)")
    public Lesson generateOtp(Long courseId, Long lessonId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Lesson lesson = course.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Generate a random 6-digit OTP
        String otp = String.format("%06d", (int)(Math.random() * 1000000));
        lesson.setOtp(otp);

        courseRepository.save(course);
        return lesson;
    }

    @PreAuthorize("hasRole('STUDENT') and @courseSecurityService.isEnrolledInCourse(authentication.principal.username, #courseId)")
    public boolean validateOtp(Long courseId, Long lessonId, String otp) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Lesson lesson = course.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        return otp.equals(lesson.getOtp());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId)")
    public List<String> getEnrolledStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getEnrolledStudents().stream().collect(Collectors.toList());
    }

//    public Course addStudentsToCourse(Long courseId, List<String> studentUsernames) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
//
//        // Add students to the course
//        course.getEnrolledStudents().addAll(studentUsernames);
//
//        // Save the updated course
//        return courseRepository.save(course);
//    }

    public Course addLessonsToCourse(Long courseId, List<Lesson> lessons) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Set the course reference for each lesson and save the lessons
        lessons.forEach(lesson -> {
            lesson.setCourse(course);
            lessonRepository.save(lesson);
        });

        // Refresh the course with the updated lessons
        course.getLessons().addAll(lessons);
        return courseRepository.save(course);
    }
}