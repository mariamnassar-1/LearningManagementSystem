package com.lms.learning_management_system;

import com.lms.learning_management_system.controller.EnrollmentController;
import com.lms.learning_management_system.model.Course;
import com.lms.learning_management_system.model.Enrollment;
import com.lms.learning_management_system.service.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(EnrollmentController.class)
class EnrollmentTest {
    @MockitoBean
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @Autowired
    private MockMvc mockMvc;

    private static final String STUDENT = "studentUser";
    private static final String FIRST_TITLE_JSON_PATH = "$[0].title";

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void getEnrolledCoursesTest() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        when(enrollmentService.getEnrolledCourses(STUDENT)).thenReturn(List.of(course));

        mockMvc.perform(get("/api/enrollments/enrolled").principal(() -> STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_TITLE_JSON_PATH).value("Test Course"));

        verify(enrollmentService, times(1)).getEnrolledCourses(STUDENT);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllEnrollments_returnsEnrollmentsList() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        List<Enrollment> enrollments = List.of(enrollment);

        when(enrollmentService.getAllEnrollments()).thenReturn(enrollments);

        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(enrollmentService, times(1)).getAllEnrollments();
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void getEnrollmentsByStudentId_returnsEnrollments() throws Exception {
        Long studentId = 1L;
        Enrollment enrollment = new Enrollment();
        enrollment.setId(10L);
        List<Enrollment> enrollments = List.of(enrollment);

        when(enrollmentService.getEnrollmentsByStudentId(studentId)).thenReturn(enrollments);

        mockMvc.perform(get("/api/enrollments/student/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));

        verify(enrollmentService, times(1)).getEnrollmentsByStudentId(studentId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getEnrollmentsByCourseId_returnsEnrollments() throws Exception {
        Long courseId = 5L;
        Enrollment enrollment = new Enrollment();
        enrollment.setId(20L);
        List<Enrollment> enrollments = List.of(enrollment);

        when(enrollmentService.getEnrollmentsByCourseId(courseId)).thenReturn(enrollments);

        mockMvc.perform(get("/api/enrollments/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L));

        verify(enrollmentService, times(1)).getEnrollmentsByCourseId(courseId);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void enrollStudentInCourse_success() throws Exception {
        Long courseId = 1L;
        Enrollment enrollment = new Enrollment();
        enrollment.setId(100L);
        Course course = new Course();
        course.setId(courseId);
        enrollment.setCourse(course);

        when(enrollmentService.enrollStudentInCourse(STUDENT, courseId)).thenReturn(enrollment);

        mockMvc.perform(post("/api/enrollments/enroll")
                .param("courseId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).enrollStudentInCourse(STUDENT, courseId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void unenrollStudent_success() throws Exception {
        Long studentId = 1L;
        Long courseId = 2L;

        doNothing().when(enrollmentService).unenrollStudent(studentId, courseId);

        mockMvc.perform(delete("/api/enrollments/unenroll")
                        .param("studentId", String.valueOf(studentId))
                        .param("courseId", String.valueOf(courseId))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).unenrollStudent(studentId, courseId);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void getEnrolledCourses_returnsCourses() throws Exception {
        Course course = new Course();
        course.setId(3L);
        course.setTitle("Course Title");

        when(enrollmentService.getEnrolledCourses(STUDENT)).thenReturn(List.of(course));

        mockMvc.perform(get("/api/enrollments/enrolled").principal(() -> STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].title").value("Course Title"));

        verify(enrollmentService, times(1)).getEnrolledCourses(STUDENT);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getEnrolledStudents_success() throws Exception {
        Long courseId = 4L;
        List<String> enrolledStudents = List.of("studentUser1", "studentUser2");

        when(enrollmentService.getEnrolledStudents(courseId)).thenReturn(enrolledStudents);

        mockMvc.perform(get("/api/enrollments/{courseId}/students", courseId))
                .andExpect(jsonPath("$[0]").value("studentUser1"))
                .andExpect(jsonPath("$[1]").value("studentUser2"));

        verify(enrollmentService, times(1)).getEnrolledStudents(courseId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getEnrolledStudents_Exception() throws Exception {
        Long courseId = 4L;

        when(enrollmentService.getEnrolledStudents(courseId))
                .thenThrow(new EntityNotFoundException("Course not found"));

        mockMvc.perform(get("/api/enrollments/{courseId}/students", courseId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));

        verify(enrollmentService, times(1)).getEnrolledStudents(courseId);
    }
}

