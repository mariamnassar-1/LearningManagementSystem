package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.Assignment;
import com.lms.LearningManagementSystem.service.AssignmentService;
import com.lms.LearningManagementSystem.model.Quiz;
import com.lms.LearningManagementSystem.service.QuizService;
import com.lms.LearningManagementSystem.model.Question;
import com.lms.LearningManagementSystem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Assessment")
public class AssessmentController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private QuizService quizService;
    @PostMapping("/questions")
    public Question addQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    @GetMapping("/questions/random")
    public Question getRandomQuestion() {
        return questionService.getRandomQuestion();
    }

    @PostMapping("/assignments")
    public Assignment submitAssignment(@RequestBody Assignment assignment) {
        return assignmentService.saveAssignment(assignment);
    }

    @PostMapping("/assignments/{id}/grade")
    public Assignment gradeAssignment(@PathVariable Long id, @RequestParam Double grade, @RequestParam String feedback) {
        return assignmentService.gradeAssignment(id, grade, feedback);
    }

    @GetMapping("/assignments")
    public List<Assignment> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    @PostMapping("/quizzes")
    public Quiz addQuiz(@RequestBody Quiz quiz) {
        return quizService.saveQuiz(quiz);
    }

    @GetMapping("/quizzes/{id}")
    public Quiz getQuiz(@PathVariable Long id) {
        return quizService.getQuiz(id);
    }

    @GetMapping("/quizzes")
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @PutMapping("/quizzes/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz updatedQuiz) {
        Quiz quiz = quizService.updateQuiz(id, updatedQuiz);
        return ResponseEntity.ok(quiz);
    }


}
