package com.lms.learning_management_system.repository;
import com.lms.learning_management_system.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {}
