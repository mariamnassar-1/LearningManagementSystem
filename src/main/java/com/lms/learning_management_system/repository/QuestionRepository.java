package com.lms.learning_management_system.repository;

import com.lms.learning_management_system.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuestionRepository extends JpaRepository<Question, Long> {}
