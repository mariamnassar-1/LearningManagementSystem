package com.lms.learning_management_system.service;

import com.lms.learning_management_system.model.Quiz;
import com.lms.learning_management_system.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Quiz getQuiz(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Quiz not found"));
    }
    public Quiz updateQuiz(Long id, Quiz updatedQuiz) {
        // Check if the quiz with the given ID exists
        Optional<Quiz> existingQuizOptional = quizRepository.findById(id);

        if (existingQuizOptional.isPresent()) {
            Quiz existingQuiz = existingQuizOptional.get();

            // Update the fields of the existing quiz with the new values
            existingQuiz.setTitle(updatedQuiz.getTitle());
            existingQuiz.setDescription(updatedQuiz.getDescription());
            existingQuiz.setTotalMarks(updatedQuiz.getTotalMarks());

            // Save the updated quiz back to the database
            return quizRepository.save(existingQuiz);
        }
        throw new NoSuchElementException("Quiz not found");
    }


    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
}
