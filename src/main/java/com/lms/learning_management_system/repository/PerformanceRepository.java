package com.lms.learning_management_system.repository;

import com.lms.learning_management_system.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByEnrollmentId(Long enrollmentId);


    List<Performance> findByQuizScoreGreaterThan(Double score);


    List<Performance> findByEnrollmentIdAndAttendanceMarkedTrue(Long enrollmentId);
}
