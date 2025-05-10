package lms.step1.Repository;

import lms.step1.Model.Course;
import lms.step1.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import lms.step1.Model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourse(Course course);
}