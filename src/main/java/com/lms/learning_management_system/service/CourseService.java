package com.lms.learning_management_system.service;

import com.lms.learning_management_system.model.Course;
import com.lms.learning_management_system.model.Lesson;
import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.repository.CourseRepository;
import com.lms.learning_management_system.repository.LessonRepository;
import com.lms.learning_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            try {
                notificationService.createNotification(userService.findByUsername(studentName), subject, message);
            } catch (MailException e) {
                System.out.println("failed to send notif");
            }
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

    public Course uploadMediaFiles(Long courseId, List<MultipartFile> files) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = saveFile(file);
            fileNames.add(fileName);
        }

        course.getMediaFiles().addAll(fileNames);
        return courseRepository.save(course);
    }

    private String saveFile(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();

            String fileName = System.currentTimeMillis() + "_" + originalFileName;
            Path directoryPath = Paths.get(mediaStoragePath).toAbsolutePath().normalize();
            Files.createDirectories(directoryPath); // Ensure directory exists

            Path filePath = directoryPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file " + file.getOriginalFilename(), e);
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