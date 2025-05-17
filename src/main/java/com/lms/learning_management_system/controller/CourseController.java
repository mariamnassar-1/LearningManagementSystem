package com.lms.learning_management_system.controller;

import com.lms.learning_management_system.model.Course;
import com.lms.learning_management_system.model.Lesson;
import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.service.CourseService;
import com.lms.learning_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // FIXED: Improved error handling and instructor validation
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> createCourse(@RequestBody Course course, Principal principal) {
        String username = principal.getName();
        User instructor = userService.findByUsername(username);

        if (instructor == null) {
            return ResponseEntity.badRequest().body("Instructor not found for username: " + username);
        }

        try {
            course.setInstructor(instructor);
            Course createdCourse = courseService.createCourse(course);
            return ResponseEntity.ok(createdCourse);
        } catch (Exception e) {
            e.printStackTrace(); // For debugging; consider using a logger in production
            return ResponseEntity.badRequest().body("Failed to create course: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #id)")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            Course updatedCourse = courseService.updateCourse(id, course);
            return ResponseEntity.ok(updatedCourse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #id) or hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getInstructorCourses(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(courseService.getCoursesByInstructor(username));
    }


    @PostMapping("/{courseId}/media")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId)")
    public ResponseEntity<Course> uploadMediaFiles(
            @PathVariable Long courseId,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(courseService.uploadMediaFiles(courseId, files));
    }

    @GetMapping("/media/{fileName}")
    public ResponseEntity<Resource> serveMediaFile(@PathVariable String fileName) {
        Path filePath = Paths.get("/MediaStorage", fileName);
        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/generate-otp")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Lesson> generateOtp(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return ResponseEntity.ok(courseService.generateOtp(courseId, lessonId));
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/validate-otp")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> validateOtp(@PathVariable Long courseId, @PathVariable Long lessonId, @RequestBody String otp) {
        return ResponseEntity.ok(courseService.validateOtp(courseId, lessonId, otp));
    }

    @PostMapping("/{courseId}/lessons")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId)")
    public ResponseEntity<Course> addLessonsToCourse(@PathVariable Long courseId, @RequestBody List<Lesson> lessons) {
        return ResponseEntity.ok(courseService.addLessonsToCourse(courseId, lessons));
    }
}