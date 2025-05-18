package com.lms.learning_management_system;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.lms.learning_management_system.controller.CourseController;
import com.lms.learning_management_system.model.Course;
import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.service.CourseService;
import com.lms.learning_management_system.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CourseTest {
    private static final String INSTRUCTOR = "instructor";
    private static final String STUDENT = "student";
    private static final String TITLE_JSON_PATH = "$.title";
    private static final String FIRST_TITLE_JSON_PATH = "$[0].title";
    private static final String COURSE_ENDPOINT = "/api/courses/{id}";
    private static final String COURSE_1 = "Course 1";
    private static final String VIDEO_1 = "video1.mp4";
    private static final String DOCUMENT_1 = "document1.pdf";

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @Mock
    private EnrollmentService enrollmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    @WithMockUser(username = INSTRUCTOR, roles = {"INSTRUCTOR"})
    void createCourse() throws Exception {
        Course course = new Course();
        course.setTitle("New Course");
        User instructor = new User();
        instructor.setUsername(INSTRUCTOR);
        course.setInstructor(instructor);

        when(courseService.createCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE_JSON_PATH).value("New Course"));

        verify(courseService, times(1)).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(username = INSTRUCTOR, roles = {"INSTRUCTOR"})
    void updateCourse() throws Exception {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Updated Course");
        User instructor = new User();
        instructor.setUsername(INSTRUCTOR);
        course.setInstructor(instructor);

        when(courseService.updateCourse(eq(courseId), any(Course.class))).thenReturn(course);

        mockMvc.perform(put(COURSE_ENDPOINT, courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE_JSON_PATH).value("Updated Course"));

        verify(courseService, times(1)).updateCourse(eq(courseId), any(Course.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).deleteCourse(courseId);

        mockMvc.perform(delete(COURSE_ENDPOINT, courseId))
                .andExpect(status().isOk());

        verify(courseService, times(1)).deleteCourse(courseId);
    }

    @Test
    void getAllCourses() throws Exception {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle(COURSE_1);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");

        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course1, course2));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_TITLE_JSON_PATH).value(COURSE_1))
                .andExpect(jsonPath("$[1].title").value("Course 2"));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void getCourseById() throws Exception {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle(COURSE_1);

        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));

        mockMvc.perform(get(COURSE_ENDPOINT, courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE_JSON_PATH).value(COURSE_1));

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    void getInstructorCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Instructor Course");

        when(courseService.getCoursesByInstructor(INSTRUCTOR))
                .thenReturn(List.of(course));

        mockMvc.perform(get("/api/courses/instructor").principal(() -> INSTRUCTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_TITLE_JSON_PATH).value("Instructor Course"));

        verify(courseService, times(1)).getCoursesByInstructor(INSTRUCTOR);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void getEnrolledCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        when(enrollmentService.getEnrolledCourses(STUDENT))
                .thenReturn(List.of(course));

        mockMvc.perform(get("/api/courses/enrolled")
                        .principal(() -> STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_TITLE_JSON_PATH).value("Test Course"));

        verify(enrollmentService, times(1)).getEnrolledCourses(STUDENT);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void enrollInCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(enrollmentService).enrollStudentInCourse(STUDENT, courseId);

        mockMvc.perform(post("/api/courses/{id}/enroll", courseId)
                        .principal(() -> STUDENT))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).enrollStudentInCourse(STUDENT, courseId);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void unenrollFromCourse() throws Exception {
        Long studentId = 10L;
        Long courseId = 1L;

        doNothing().when(enrollmentService).unenrollStudent(studentId, courseId);

        mockMvc.perform(post("/api/courses/{id}/unenroll", courseId)
                        .principal(() -> STUDENT))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).unenrollStudent(studentId, courseId);
    }

    @Test
    void uploadMediaFiles() throws Exception {
        Long courseId = 1L;

        // Mock file data
        MockMultipartFile file1 = new MockMultipartFile("files", VIDEO_1, MediaType.MULTIPART_FORM_DATA_VALUE, "Dummy video content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", DOCUMENT_1, MediaType.MULTIPART_FORM_DATA_VALUE, "Dummy document content".getBytes());

        Course course = new Course();
        course.setId(courseId);
        course.setMediaFiles(Arrays.asList(VIDEO_1, DOCUMENT_1));

        when(courseService.uploadMediaFiles(eq(courseId), anyList())).thenReturn(course);

        mockMvc.perform(multipart("/api/courses/{courseId}/media", courseId)
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.mediaFiles[0]").value(VIDEO_1))
                .andExpect(jsonPath("$.mediaFiles[1]").value(DOCUMENT_1));

        verify(courseService, times(1)).uploadMediaFiles(eq(courseId), anyList());
    }
}
