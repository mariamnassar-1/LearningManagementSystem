package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Question;
import com.lms.LearningManagementSystem.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question getRandomQuestion() {
        List<Question> questions = questionRepository.findAll();
        if (questions.isEmpty()) throw new RuntimeException("No questions available");
        Random random = new Random();
        return questions.get(random.nextInt(questions.size()));
    }
}

