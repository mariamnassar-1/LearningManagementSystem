

package com.lms.learning_management_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.learning_management_system.controller.AssessmentController;
import com.lms.learning_management_system.model.Assignment;

import com.lms.learning_management_system.model.Question;
import com.lms.learning_management_system.model.Quiz;
import com.lms.learning_management_system.service.AssignmentService;
import com.lms.learning_management_system.service.QuestionService;
import com.lms.learning_management_system.service.QuizService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({MockitoExtension.class})
class AssessmentTest {
    @InjectMocks
    private AssessmentController assessmentController;
    @Mock
    private QuestionService questionService;
    @Mock
    private QuizService quizService;
    @Mock
    private AssignmentService assignmentService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    AssessmentTest() {
    }

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new Object[]{this.assessmentController}).build();
        this.objectMapper = new ObjectMapper();

    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void addQuestion() throws Exception {
        Question question = new Question();
        question.setText("What is Java?");
        question.setOptions("A. Language, B. Platform, C. Library, D. None");
        question.setCorrectAnswer("A");

        Mockito.when(this.questionService.saveQuestion(Mockito.any(Question.class))).thenReturn(question);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("What is Java?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.options").value("A. Language, B. Platform, C. Library, D. None"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.correctAnswer").value("A"));

        Mockito.verify(this.questionService, Mockito.times(1)).saveQuestion(Mockito.any(Question.class));
    }






    @Test
    void getRandomQuestion() throws Exception {
        Question question = new Question();
        question.setText("What is C++?");
        question.setOptions("A. Language, B. Platform, C. Library, D. None");
        question.setCorrectAnswer("A");

        Mockito.when(this.questionService.getRandomQuestion()).thenReturn(question);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/Assessment/questions/random"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("What is C++?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.options").value("A. Language, B. Platform, C. Library, D. None"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.correctAnswer").value("A"));

        Mockito.verify(this.questionService, Mockito.times(1)).getRandomQuestion();
    }


    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void submitAssignment() throws Exception {
        Assignment assignment = new Assignment();
        assignment.setTitle("Assignment 1");
        assignment.setStudentName("Muhammad Fathi");
        assignment.setContent("Any Content");
        assignment.setFeedback("Great work!");
        assignment.setGrade(90.0);

        Mockito.when(this.assignmentService.saveAssignment(Mockito.any(Assignment.class))).thenReturn(assignment);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(assignment)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Assignment 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studentName").value("Muhammad Fathi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Any Content"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value(90.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.feedback").value("Great work!"));

        Mockito.verify(this.assignmentService, Mockito.times(1)).saveAssignment(Mockito.any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void gradeAssignment() throws Exception {
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        assignment.setTitle("Assignment 1");
        assignment.setStudentName("Muhammad Fathi");
        assignment.setContent("Any Content");
        assignment.setFeedback("Good job!");
        assignment.setGrade(95.0);

        Mockito.when(this.assignmentService.gradeAssignment(Mockito.eq(1L), Mockito.anyDouble(), Mockito.anyString())).thenReturn(assignment);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/assignments/1/grade")
                        .param("grade", "95.0")
                        .param("feedback", "Good job!"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value(95.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.feedback").value("Good job!"));

        Mockito.verify(this.assignmentService, Mockito.times(1)).gradeAssignment(Mockito.eq(1L), Mockito.anyDouble(), Mockito.anyString());
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void getAllAssignmentsAsInstructor() throws Exception {
        Assignment assignment1 = new Assignment();
        assignment1.setId(1L);
        assignment1.setTitle("Assignment 1");
        assignment1.setStudentName("Muhammad Fathi");
        assignment1.setContent("Content 1");
        assignment1.setGrade(90.0);
        assignment1.setFeedback("Well done");

        Assignment assignment2 = new Assignment();
        assignment2.setId(2L);
        assignment2.setTitle("Assignment 2");
        assignment2.setStudentName("Abdo Ali");
        assignment2.setContent("Content 2");
        assignment2.setGrade(85.0);
        assignment2.setFeedback("Needs improvement");

        List<Assignment> assignments = Arrays.asList(assignment1, assignment2);
        Mockito.when(this.assignmentService.getAllAssignments()).thenReturn(assignments);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/Assessment/assignments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2));

        Mockito.verify(this.assignmentService, Mockito.times(1)).getAllAssignments();
    }



    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void addQuizAsInstructor() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Basics Quiz");
        quiz.setDescription("A quiz about basic Java concepts.");
        quiz.setTotalMarks(100L);

        Mockito.when(this.quizService.saveQuiz(Mockito.any(Quiz.class))).thenReturn(quiz);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(quiz)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Java Basics Quiz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("A quiz about basic Java concepts."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalMarks").value(100));

        Mockito.verify(this.quizService, Mockito.times(1)).saveQuiz(Mockito.any(Quiz.class));
    }

    @Test
    void getQuizById() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Java Basics Quiz");
        quiz.setDescription("A quiz about basic Java concepts.");
        quiz.setTotalMarks(100L);
        Mockito.when(this.quizService.getQuiz(1L)).thenReturn(quiz);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/Assessment/quizzes/1", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$.title", new Object[0]).value("Java Basics Quiz")).andExpect(MockMvcResultMatchers.jsonPath("$.description", new Object[0]).value("A quiz about basic Java concepts.")).andExpect(MockMvcResultMatchers.jsonPath("$.totalMarks", new Object[0]).value(100));
        ((QuizService)Mockito.verify(this.quizService, Mockito.times(1))).getQuiz(1L);
    }

    @Test
    void getAllQuizzes() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Java Basics Quiz");
        quiz1.setDescription("A quiz about basic Java concepts.");
        quiz1.setTotalMarks(100L);
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Advanced Java Quiz");
        quiz2.setDescription("A quiz about advanced Java concepts.");
        quiz2.setTotalMarks(100L);
        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);
        Mockito.when(this.quizService.getAllQuizzes()).thenReturn(quizzes);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/Assessment/quizzes", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$[1].id", new Object[0]).value(2));
        ((QuizService)Mockito.verify(this.quizService, Mockito.times(1))).getAllQuizzes();
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void updateQuizAsInstructor() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Updated Java Basics Quiz");
        quiz.setDescription("An updated quiz about basic Java concepts.");
        quiz.setTotalMarks(150L);

        Mockito.when(this.quizService.updateQuiz(Mockito.eq(1L), Mockito.any(Quiz.class))).thenReturn(quiz);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/Assessment/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(quiz)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Java Basics Quiz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("An updated quiz about basic Java concepts."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalMarks").value(150));

        Mockito.verify(this.quizService, Mockito.times(1)).updateQuiz(Mockito.eq(1L), Mockito.any(Quiz.class));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateQuizAsAdmin() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Updated Java Basics Quiz");
        quiz.setDescription("An updated quiz about basic Java concepts.");
        quiz.setTotalMarks(150L);

        Mockito.when(this.quizService.updateQuiz(Mockito.eq(1L), Mockito.any(Quiz.class))).thenReturn(quiz);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/Assessment/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(quiz)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Java Basics Quiz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("An updated quiz about basic Java concepts."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalMarks").value(150));

        Mockito.verify(this.quizService, Mockito.times(1)).updateQuiz(Mockito.eq(1L), Mockito.any(Quiz.class));
    }

}
