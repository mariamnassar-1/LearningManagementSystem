//package com.lms.LearningManagementSystem.controller;
//
//import com.lms.LearningManagementSystem.model.User;
//import com.lms.LearningManagementSystem.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "*")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<User>> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        return userService.getUserById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PutMapping("/{username}")
//    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
//    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User user) {
//        try {
//            userService.updateUser(username, user);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        try {
//            userService.deleteUser(id);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/profile")
//    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
//        return ResponseEntity.ok(userService.findByUsername(authentication.getName()));
//    }
//
//    @PutMapping("/{id}/role")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String role) {
//        try {
//            User updatedUser = userService.updateUserRole(id, role);
//            return ResponseEntity.ok(updatedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//}
