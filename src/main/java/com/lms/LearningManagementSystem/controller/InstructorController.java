package com.lms.LearningManagementSystem.controller;


import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructor")
@CrossOrigin(origins = "*")
public class InstructorController {

    @Autowired
    CourseService courseService;

    //create course
    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course, Authentication authentication) {
        //pass the instructor's id to the course
        User instructor = new User();
        instructor.setUsername(authentication.getName());
        course.setInstructor(instructor);

        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.ok(createdCourse);
    }
    //delete course
    //update course
    //generate OTP
    //make assignment
    //make quiz
    //grade assignment
    //get assignments
    //get notified
}
