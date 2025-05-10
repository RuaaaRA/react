package lms.step1.Service;

import lms.step1.DTO.*;

import java.util.List;

public interface EnrollmentService {

    void enroll(String studentUsername, Long courseId);

    void unenroll(String studentUsername, Long courseId);

    void markComplete(String studentUsername, Long courseId);

    List<EnrollmentStatusDTO> getMyEnrollments(String studentUsername);

    List<EnrollmentStatusDTO> getCourseEnrollments(Long courseId, String requesterUsername);

    void enrollStudentByAdminOrInstructor(EnrollStudentDTO dto, String requesterUsername);

    EnrollmentStatusDTO getMyEnrollmentForCourse(String username, Long courseId);

    void removeStudentFromCourse(String instructorUsername, RemoveStudentDTO dto);
}
