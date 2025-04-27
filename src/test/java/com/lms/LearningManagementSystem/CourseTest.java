package com.lms.LearningManagementSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.lms.LearningManagementSystem.controller.CourseController;
import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.service.CourseService;
import com.lms.LearningManagementSystem.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CourseTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void createCourse() throws Exception {
        Course course = new Course();
        course.setTitle("New Course");
        User instructor = new User();
        instructor.setUsername("instructor");
        course.setInstructor(instructor);

        when(courseService.createCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Course"));

        verify(courseService, times(1)).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void updateCourse() throws Exception {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Updated Course");
        User instructor = new User();
        instructor.setUsername("instructor");
        course.setInstructor(instructor);

        when(courseService.updateCourse(eq(courseId), any(Course.class))).thenReturn(course);

        mockMvc.perform(put("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Course"));

        verify(courseService, times(1)).updateCourse(eq(courseId), any(Course.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).deleteCourse(courseId);

        mockMvc.perform(delete("/api/courses/{id}", courseId))
                .andExpect(status().isOk());

        verify(courseService, times(1)).deleteCourse(courseId);
    }

    @Test
    void getAllCourses() throws Exception {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");

        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course1, course2));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].title").value("Course 2"));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void getCourseById() throws Exception {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Course 1");

        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));

        mockMvc.perform(get("/api/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Course 1"));

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    void getInstructorCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Instructor Course");

        when(courseService.getCoursesByInstructor("instructor"))
                .thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/instructor").principal(() -> "instructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Instructor Course"));

        verify(courseService, times(1)).getCoursesByInstructor("instructor");
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void getEnrolledCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        when(courseService.getEnrolledCourses("student"))
                .thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/enrolled")
                        .principal(() -> "student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Course"));

        verify(courseService, times(1)).getEnrolledCourses("student");
    }


    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void enrollInCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).enrollStudent(courseId, "student");

        mockMvc.perform(post("/api/courses/{id}/enroll", courseId)
                        .principal(() -> "student"))
                .andExpect(status().isOk());

        verify(courseService, times(1)).enrollStudent(courseId, "student");
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void unenrollFromCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).unenrollStudent(courseId, "student");

        mockMvc.perform(post("/api/courses/{id}/unenroll", courseId)
                        .principal(() -> "student"))
                .andExpect(status().isOk());

        verify(courseService, times(1)).unenrollStudent(courseId, "student");
    }

    @Test
    void uploadMediaFiles() throws Exception {
        Long courseId = 1L;

        // Mock file data
        MockMultipartFile file1 = new MockMultipartFile("files", "video1.mp4", MediaType.MULTIPART_FORM_DATA_VALUE, "Dummy video content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "document1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Dummy document content".getBytes());

        Course course = new Course();
        course.setId(courseId);
        course.setMediaFiles(Arrays.asList("video1.mp4", "document1.pdf"));

        when(courseService.uploadMediaFiles(eq(courseId), anyList())).thenReturn(course);

        mockMvc.perform(multipart("/api/courses/{courseId}/media", courseId)
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.mediaFiles[0]").value("video1.mp4"))
                .andExpect(jsonPath("$.mediaFiles[1]").value("document1.pdf"));

        verify(courseService, times(1)).uploadMediaFiles(eq(courseId), anyList());
    }
}
