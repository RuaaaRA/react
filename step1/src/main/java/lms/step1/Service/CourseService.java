package lms.step1.Service;

import lms.step1.DTO.AssignInstructorDTO;
import lms.step1.DTO.CourseDTO;
import java.util.List;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(Long courseId, CourseDTO courseDTO);
    void deleteCourse(Long courseId);
    List<CourseDTO> getAllCourses();
    CourseDTO getCourseById(Long courseId);
    void assignInstructor(AssignInstructorDTO request);
    void sendCourseUpdateNotification(String email, String courseTitle, String description, int duration);

}
