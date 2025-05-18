package com.lms.learning_management_system.controller;


import com.lms.learning_management_system.model.Course;
import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.service.CourseService;
import com.lms.learning_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //create user
    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok("User created successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    //get course by id
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        try {
            User updatedUser = userService.updateUserRole(id, role);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while deleting the user.");
        }
    }
}
