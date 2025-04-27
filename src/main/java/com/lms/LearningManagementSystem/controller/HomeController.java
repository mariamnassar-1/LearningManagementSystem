package com.lms.LearningManagementSystem.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "Welcome to your dashboard!";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "You do not have permission to access this page.";
    }

}
