package com.lms.learning_management_system.service;

import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.model.User.Role;
import com.lms.learning_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USERNAME_EXISTS = "Username already exists";
    private static final String EMAIL_EXISTS = "Email already exists";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Admin only can get all users
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Admin only can get user by ID
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Public registration endpoint
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException(USERNAME_EXISTS);
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException(EMAIL_EXISTS);
        }
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Admin only can update user roles
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        user.setRole(Role.valueOf(role.toUpperCase()));
        return userRepository.save(user);
    }

    // Users can only update their own profiles
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public void updateUser(String username, User user) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        
        // Don't allow role changes through this method
        user.setRole(existingUser.getRole());
        
        // Only encode password if it's changed
        if (!user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        userRepository.save(user);
    }

    // Admin only can delete users
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException(USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    // Users can find their own profiles, admins can find any profile
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    // Public registration endpoint
    public void registerUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
