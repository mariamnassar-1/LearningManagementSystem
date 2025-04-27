package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Question;
import com.lms.LearningManagementSystem.service.QuestionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



public interface QuestionRepository extends JpaRepository<Question, Long> {}
