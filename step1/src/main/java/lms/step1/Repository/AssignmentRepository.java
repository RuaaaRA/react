package lms.step1.Repository;

import lms.step1.Model.Assignment;
import lms.step1.Model.Course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourse(Course course);

}
