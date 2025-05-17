package com.lms.learning_management_system.service;

import com.lms.learning_management_system.model.Assignment;
import com.lms.learning_management_system.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private NotificationService notificationService;

    public Assignment saveAssignment(Assignment assignment) {
        String subject = "New Assignment";
        String message = assignment.getContent();
        notificationService.createNotification(assignment.getStudent(), subject, message);
        return assignmentRepository.save(assignment);
    }

    public Assignment gradeAssignment(Long id, Double grade, String feedback) {
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setGrade(grade);
        assignment.setFeedback(feedback);

        String subject = "Assignment Grade";
        String message = "result for "+assignment.getTitle()+"assignment\n"+
                            "Grade: "+grade+
                            "\nfeedback: " +feedback;
        notificationService.createNotification(assignment.getStudent(), subject, message);

        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
}