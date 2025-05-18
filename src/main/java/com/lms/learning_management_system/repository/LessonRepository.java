package com.lms.learning_management_system.repository;

import com.lms.learning_management_system.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
