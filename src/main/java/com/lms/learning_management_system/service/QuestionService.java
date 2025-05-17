package com.lms.learning_management_system.service;

import com.lms.learning_management_system.model.Question;
import com.lms.learning_management_system.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    private final Random random = new Random(); // Reused instance

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question getRandomQuestion() {
        List<Question> questions = questionRepository.findAll();
        if (questions.isEmpty()) throw new NoSuchElementException("No questions available");

        return questions.get(random.nextInt(questions.size()));
    }
}

