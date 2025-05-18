package com.lms.learning_management_system.repository;

import com.lms.learning_management_system.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorUsername(String instructorUsername);
    List<Course> findByEnrolledStudentsContaining(String username);
}
