//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.lms.learning_management_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.learning_management_system.controller.AssessmentController;
import com.lms.learning_management_system.model.Assignment;
import com.lms.learning_management_system.model.Course;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({MockitoExtension.class})
class AssessmentTest {
    private static final String QUESTION_OPTIONS = "A. Language, B. Platform, C. Library, D. None";
    private static final String ASSIGNMENT_1 = "Assignment 1";
    private static final String STUDENT_NAME = "Muhammad Fathi";
    private static final String ANY_CONTENT = "Any Content";
    private static final String GREAT_WORK = "Great work!";
    private static final String GOOD_JOB = "Good job!";
    private static final String ASSIGNMENTS_ENDPOINT = "/api/Assessment/assignments";
    private static final String TITLE_JSON_PATH = "$.title";
    private static final String JAVA_BASICS_QUIZ = "Java Basics Quiz";
    private static final String QUIZ_DESCRIPTION = "A quiz about basic Java concepts.";
    private static final String TOTAL_MARKS_JSON_PATH = "$.totalMarks";
    private static final String DESCRIPTION_JSON_PATH = "$.description";

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
    void addQuestion() throws Exception {
        Question question = new Question();
        question.setText("What is Java?");
        question.setOptions(QUESTION_OPTIONS);
        question.setCorrectAnswer("A");
        Mockito.when(this.questionService.saveQuestion((Question)Mockito.any(Question.class))).thenReturn(question);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/questions", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(question))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath(TITLE_JSON_PATH, new Object[0]).value("What is Java?")).andExpect(MockMvcResultMatchers.jsonPath("$.options", new Object[0]).value(QUESTION_OPTIONS)).andExpect(MockMvcResultMatchers.jsonPath("$.correctAnswer", new Object[0]).value("A"));
        ((QuestionService)Mockito.verify(this.questionService, Mockito.times(1))).saveQuestion((Question)Mockito.any(Question.class));
    }

    @Test
    void getRandomQuestion() throws Exception {
        Question question = new Question();
        question.setText("What is C++?");
        question.setOptions(QUESTION_OPTIONS);
        question.setCorrectAnswer("A");
        Mockito.when(this.questionService.getRandomQuestion()).thenReturn(question);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/Assessment/questions/random", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath(TITLE_JSON_PATH, new Object[0]).value("What is C++?")).andExpect(MockMvcResultMatchers.jsonPath("$.options", new Object[0]).value(QUESTION_OPTIONS)).andExpect(MockMvcResultMatchers.jsonPath("$.correctAnswer", new Object[0]).value("A"));
        ((QuestionService)Mockito.verify(this.questionService, Mockito.times(1))).getRandomQuestion();
    }

    @Test
    void submitAssignment() throws Exception {
        Assignment assignment = new Assignment();
        assignment.setTitle(ASSIGNMENT_1);
        assignment.setStudentName(STUDENT_NAME);
        assignment.setContent(ANY_CONTENT);
        assignment.setFeedback(GREAT_WORK);
        assignment.setGrade(90.0);
        Mockito.when(this.assignmentService.saveAssignment((Assignment)Mockito.any(Assignment.class))).thenReturn(assignment);
        this.mockMvc.perform(MockMvcRequestBuilders.post(ASSIGNMENTS_ENDPOINT, new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(assignment))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath(TITLE_JSON_PATH, new Object[0]).value(ASSIGNMENT_1)).andExpect(MockMvcResultMatchers.jsonPath("$.studentName", new Object[0]).value(STUDENT_NAME)).andExpect(MockMvcResultMatchers.jsonPath("$.content", new Object[0]).value(ANY_CONTENT)).andExpect(MockMvcResultMatchers.jsonPath("$.grade", new Object[0]).value(90.0)).andExpect(MockMvcResultMatchers.jsonPath("$.feedback", new Object[0]).value(GREAT_WORK));
        ((AssignmentService)Mockito.verify(this.assignmentService, Mockito.times(1))).saveAssignment((Assignment)Mockito.any(Assignment.class));
    }

    @Test
    void gradeAssignment() throws Exception {
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        assignment.setTitle(ASSIGNMENT_1);
        assignment.setStudentName(STUDENT_NAME);
        assignment.setContent(ANY_CONTENT);
        assignment.setFeedback(GOOD_JOB);
        assignment.setGrade(95.0);
        Mockito.when(this.assignmentService.gradeAssignment(Mockito.eq(1L), Mockito.anyDouble(), Mockito.anyString())).thenReturn(assignment);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/assignments/1/grade", new Object[0]).param("grade", new String[]{"95.0"}).param("feedback", new String[]{GOOD_JOB})).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$.grade", new Object[0]).value(95.0)).andExpect(MockMvcResultMatchers.jsonPath("$.feedback", new Object[0]).value(GOOD_JOB));
        ((AssignmentService)Mockito.verify(this.assignmentService, Mockito.times(1))).gradeAssignment(Mockito.eq(1L), Mockito.anyDouble(), Mockito.anyString());
    }

    @Test
    void getAllAssignments() throws Exception {
        Assignment assignment1 = new Assignment();
        assignment1.setId(1L);
        assignment1.setTitle(ASSIGNMENT_1);
        assignment1.setStudentName(STUDENT_NAME);
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
        this.mockMvc.perform(MockMvcRequestBuilders.get(ASSIGNMENTS_ENDPOINT, new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$[1].id", new Object[0]).value(2));
        ((AssignmentService)Mockito.verify(this.assignmentService, Mockito.times(1))).getAllAssignments();
    }

    @Test
    void submitAssignmentWithCourse() throws Exception {
        Course course = new Course();
        course.setTitle("English Course");
        course.setDescription("Description of English Course");
        Assignment assignment = new Assignment();
        assignment.setTitle(ASSIGNMENT_1);
        assignment.setStudentName("Mohamed Ahmed");
        assignment.setContent("Content of the assignment.");
        assignment.setFeedback(GREAT_WORK);
        assignment.setGrade(90.0);
        assignment.setCourse(course);
        Mockito.when(this.assignmentService.saveAssignment((Assignment)Mockito.any(Assignment.class))).thenReturn(assignment);
        this.mockMvc.perform(MockMvcRequestBuilders.post(ASSIGNMENTS_ENDPOINT, new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(assignment))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.course.title", new Object[0]).value("English Course"));
        ((AssignmentService)Mockito.verify(this.assignmentService, Mockito.times(1))).saveAssignment((Assignment)Mockito.any(Assignment.class));
    }

    @Test
    void addQuiz() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setTitle(JAVA_BASICS_QUIZ);
        quiz.setDescription(QUIZ_DESCRIPTION);
        quiz.setTotalMarks(100L);
        Mockito.when(this.quizService.saveQuiz((Quiz)Mockito.any(Quiz.class))).thenReturn(quiz);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/Assessment/quizzes", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(quiz))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath(TITLE_JSON_PATH, new Object[0]).value(JAVA_BASICS_QUIZ)).andExpect(MockMvcResultMatchers.jsonPath(DESCRIPTION_JSON_PATH, new Object[0]).value(QUIZ_DESCRIPTION)).andExpect(MockMvcResultMatchers.jsonPath(TOTAL_MARKS_JSON_PATH, new Object[0]).value(100));
        ((QuizService)Mockito.verify(this.quizService, Mockito.times(1))).saveQuiz((Quiz)Mockito.any(Quiz.class));
    }

    @Test
    void getQuizById() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle(JAVA_BASICS_QUIZ);
        quiz.setDescription(QUIZ_DESCRIPTION);
        quiz.setTotalMarks(100L);
        Mockito.when(this.quizService.getQuiz(1L)).thenReturn(quiz);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/Assessment/quizzes/1", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath(TITLE_JSON_PATH, new Object[0]).value(JAVA_BASICS_QUIZ)).andExpect(MockMvcResultMatchers.jsonPath(DESCRIPTION_JSON_PATH, new Object[0]).value(QUIZ_DESCRIPTION)).andExpect(MockMvcResultMatchers.jsonPath(TOTAL_MARKS_JSON_PATH, new Object[0]).value(100));
        ((QuizService)Mockito.verify(this.quizService, Mockito.times(1))).getQuiz(1L);
    }

    @Test
    void getAllQuizzes() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle(JAVA_BASICS_QUIZ);
        quiz1.setDescription(QUIZ_DESCRIPTION);
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
    void updateQuiz() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Updated Java Basics Quiz");
        quiz.setDescription("An updated quiz about basic Java concepts.");
        quiz.setTotalMarks(150L);
        Mockito.when(this.quizService.updateQuiz(Mockito.eq(1L), (Quiz)Mockito.any(Quiz.class))).thenReturn(quiz);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/Assessment/quizzes/1", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(quiz))).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath(TITLE_JSON_PATH, new Object[0]).value("Updated Java Basics Quiz")).andExpect(MockMvcResultMatchers.jsonPath(DESCRIPTION_JSON_PATH, new Object[0]).value("An updated quiz about basic Java concepts.")).andExpect(MockMvcResultMatchers.jsonPath(TOTAL_MARKS_JSON_PATH, new Object[0]).value(150));
        ((QuizService)Mockito.verify(this.quizService, Mockito.times(1))).updateQuiz(Mockito.eq(1L), (Quiz)Mockito.any(Quiz.class));
    }
}
