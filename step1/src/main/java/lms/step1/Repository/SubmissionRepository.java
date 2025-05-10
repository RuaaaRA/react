package lms.step1.Repository;

import lms.step1.Model.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

// في حزمة lms.step1.Repository

import lms.step1.Model.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    // يمكنك إضافة استعلامات مخصصة هنا إذا لزم الأمر
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);




    
}